/*
 * Copyright � 2008, Sun Microsystems, Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *    * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *    * Neither the name of Sun Microsystems, Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



import com.sun.honeycomb.emd.parsers.QueryNode;
import com.sun.honeycomb.emd.parsers.ParseException;
import com.sun.honeycomb.emd.parsers.QueryParser;
import com.sun.honeycomb.emd.parsers.QueryExpression;
import com.sun.honeycomb.emd.parsers.QueryAnd;
import com.sun.honeycomb.emd.parsers.QueryOr;
import com.sun.honeycomb.emd.parsers.QueryText;
import com.sun.honeycomb.emd.parsers.QueryLiteral;
import com.sun.honeycomb.emd.parsers.QueryPassThrough;
import com.sun.honeycomb.emd.parsers.QueryParameter;
import com.sun.honeycomb.emd.parsers.QueryAttribute;
import com.sun.honeycomb.emd.common.EMDException;
import com.sun.honeycomb.common.NewObjectIdentifier;
import com.sun.honeycomb.common.CanonicalStrings;
import com.sun.honeycomb.emd.config.Field;
import com.sun.honeycomb.emd.config.Table;
import com.sun.honeycomb.emd.config.EMDConfigException;
import com.sun.honeycomb.emd.config.RootNamespace;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

public class QueryConvert {

    private String uniqueAttribute;
    private QueryNode query;
    private NewObjectIdentifier lastOid;
    private String lastAttribute;
    private List attributes;
    private List literals;
    private Object [] boundParameters;
    private int nextParameter;

    private VariableRegistry registry;

    private QueryConvert() {
        nextParameter = 0;
        literals = new ArrayList();
        registry = new VariableRegistry();
    }

    public QueryConvert(QueryNode nQuery,
                        NewObjectIdentifier nLastOid,
                        ArrayList nAttributes,
                        Object[] nBoundParameters) {

        this();
        uniqueAttribute = null;
        query = nQuery;
        lastOid = nLastOid;
        lastAttribute = null;
        attributes = nAttributes;
        boundParameters = nBoundParameters;
    }

    public QueryConvert(String nQuery,
                        NewObjectIdentifier nLastOid,
                        ArrayList nAttributes) 
        throws ParseException, EMDException {
        this( QueryParser.parse(nQuery), nLastOid, nAttributes, null);
    }

    public QueryConvert(String nUniqueAttribute,
                        QueryNode nQuery,
                        String nLastAttribute,
                        Object[] nBoundParameters) {
        this();

        uniqueAttribute = nUniqueAttribute;
        query = nQuery;
        lastOid = null;
        lastAttribute = nLastAttribute;
        attributes = null;
        boundParameters = nBoundParameters;
    }

    public QueryConvert(String nUniqueAttribute,
                        String nQuery,
                        String nLastAttribute) 
        throws ParseException, EMDException {
        this(nUniqueAttribute,
             QueryParser.parse(nQuery),
             nLastAttribute,
             null);
    }
    
    public VariableRegistry getRegistry() {
        return(registry);
    }
    
    public List getLiterals() {
        return(literals);
    }

    public void convert(StringBuffer sb) 
        throws EMDException {

        populateRegistry();

        if (uniqueAttribute == null) {
            // Running a query plus
            convertQueryPlus(sb);
        } else {
            // Running a select unique
            convertSelectUnique(sb);
        }
    }

    private void convertQueryPlus(StringBuffer sb)
        throws EMDException {
        sb.append("select ");
        registry.appendSelect(sb, attributes);
        sb.append(" from ");
        registry.appendFrom(sb);
        sb.append(" where ");
	
        // Put the cookie
        if (lastOid != null) {
            registry.appendCookie(sb, lastOid, literals);
            sb.append(" AND ");
        }

        sb.append(" (");

        readTree(query, sb);

        if (boundParameters != null &&
            nextParameter < boundParameters.length)
            throw new EMDException("Query specified too many dynamic parameters");

        sb.append(") ");
        registry.appendOrderBy(sb);

    }

    private void convertSelectUnique(StringBuffer sb)
        throws EMDException {
        sb.append("select distinct ");
        registry.appendRepresentation(sb, uniqueAttribute);
        sb.append(" from ");
        registry.appendFrom(sb);

        StringBuffer whereClause = new StringBuffer();
        boolean needAnd = false;
	
        // Put the cookie
        if (lastAttribute != null) {
            registry.appendRepresentation(whereClause, uniqueAttribute);
            whereClause.append(">?");
            Object lastAttributeValue = 
                CanonicalStrings.decode(lastAttribute,uniqueAttribute);
            literals.add(lastAttributeValue);
            needAnd = true;
        }

        if (query != null) {
            if (needAnd) {
                whereClause.append(" AND ");
            }
            whereClause.append(" (");
            readTree(query, whereClause);
            whereClause.append(") ");
        }

        if (whereClause.length() > 0) {
            sb.append(" where ");
            sb.append(whereClause);
        }

        sb.append(" order by ");
        registry.appendRepresentation(sb, uniqueAttribute);
    }
    
    private void populateRegistry()
        throws EMDException {

        // Put the selectUnique attribute (first, so that it is the reference variable)
        Field f = null;

        if (uniqueAttribute != null) {
            f = RootNamespace.getInstance().resolveField(uniqueAttribute);
            if (f == null) {
                throw new EMDException("Attribute ["+uniqueAttribute+"] does not exist");
            }
            registry.putVariable(f);
        }

        // Put the queryPlus attributes

        if ((attributes != null) && (attributes.size() > 0)) {
            for (int i=0; i<attributes.size(); i++) {
		String attName = (String)attributes.get(i);
                f = RootNamespace.getInstance().resolveField(attName);
                if (f == null) {
                    throw new EMDException("Attribute ["+attName+"] does not exist");
                }
                registry.putVariable(f);
            }
        }

        // Put the attributes from the query

        Stack stack = new Stack();

        if (query != null) {
            stack.push(query);
        }

        while (!stack.empty()) {
            QueryNode node = (QueryNode)stack.pop();
            if (node.getLeftChild() != null) {
                stack.push(node.getLeftChild());
            }
            if (node.getRightChild() != null) {
                stack.push(node.getRightChild());
            }
            if (node instanceof QueryExpression) {
                QueryExpression expr = (QueryExpression)node;
                registry.putVariable(expr);
            }
            if (node instanceof QueryAttribute) {
                QueryAttribute expr = (QueryAttribute)node;
                registry.putVariable(expr);
            }
        }
    }

    private void readTree(QueryNode node,
                          StringBuffer output) 
        throws EMDException {
        if (node instanceof QueryText) {
            readTree(node.getLeftChild(),output);
            output.append(" ");
            readTree(node.getRightChild(), output);
            return;
        } 

        if (node instanceof QueryPassThrough) {
            QueryPassThrough query = (QueryPassThrough)node;
            output.append(query.toSQLString());
            return;
        }
        if (node instanceof QueryLiteral) {
            QueryLiteral query = (QueryLiteral)node;
            output.append("?");
            literals.add(query.getValue());
            return;
        }

        // A Honeycomb bound-parameter is just like a literal except the value
        // comes from the list of Honeycomb boundParameters 
        // instead of from the QueryNode.
        if (node instanceof QueryParameter) {
            output.append("?");
            if (boundParameters == null)
                throw new EMDException("Cannot use ? dynamic parameters"+
                                       " without specifying literal values.");
            if (nextParameter >= boundParameters.length) 
                throw new EMDException("too many parameters at index "+nextParameter);
            literals.add(boundParameters[nextParameter]);
            nextParameter++;
            return;
        }

        if (node instanceof QueryAttribute) {
            QueryAttribute query = (QueryAttribute)node;
            registry.appendRepresentation(output, query.getAttribute());
            return;
        }

        if (node instanceof QueryAnd) {
            output.append("(");
            readTree(node.getLeftChild(), output);
            output.append(" AND ");
            readTree(node.getRightChild(), output);
            output.append(")");
            return;
        } 
        
        if (node instanceof QueryOr) {
            output.append("(");
            readTree(node.getLeftChild(), output);
            output.append(" OR ");
            readTree(node.getRightChild(), output);
            output.append(")");
            return;
        }
        
        if (node instanceof QueryExpression) {
            QueryExpression query = (QueryExpression)node;
            switch (query.getOperator()) {
            case QueryExpression.OPERATOR_EQUAL:
                registry.appendRepresentation(output, query.getAttribute());
                output.append("=?");
                literals.add(query.getValue());
                break;
            case QueryExpression.OPERATOR_NOTEQUAL:
                registry.appendRepresentation(output, query.getAttribute());
                output.append("<>?");
                literals.add(query.getValue());
                break;
            case QueryExpression.OPERATOR_LT:
                registry.appendRepresentation(output, query.getAttribute());
                output.append("<?");
                literals.add(query.getValue());
                break;
                
            case QueryExpression.OPERATOR_GT:
                registry.appendRepresentation(output, query.getAttribute());
                output.append(">?");                
                literals.add(query.getValue());
                break;
            } // switch
            return;
        } // if
        
        throw new EMDException("Invalid node ["+node.getClass().getName()+"]");
    } // readTree

    private static String normalizeValue(String input) {
        int length = input.length();
        int nbQuotes = 0;
        int index = 0;
        char c;

        for (index=0; index<length; index++) {
            c = input.charAt(index);
            if (c == '\'') {
                ++nbQuotes;
            }
        }

        if (nbQuotes == 0) {
            return("'"+input+"'");
        }

        char[] dst = new char[length+nbQuotes];
        input.getChars(0, length, dst, 0);

        int i,j;
        j = length-1;
        for (i=length+nbQuotes-1; i>=0; i--) {
            dst[i] = dst[j];
            j--;
            if (dst[i] == '\'') {
                i--;
                dst[i] = '\'';
            }
        }
	
        return("'"+new String(dst)+"'");
    }

    private static void usage() {
        System.out.println("usage: <prog> query");
        System.out.println("       <prog> attr query");
        System.out.println("       <prog> -f queryfile");
        System.out.println("       <prog> attr -f queryfile");
        System.exit(1);
    }

    private static String readFile(String fname) {
        try {
            File f = new File(fname);
            if (!f.isFile()) {
                System.err.println("not a file: " + fname);
                System.exit(1);
            }
            FileReader in = new FileReader(f);
            char cbuf[] = new char[(int)f.length()];
            in.read(cbuf, 0, cbuf.length);
            return new String(cbuf).trim();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    public static void main(String[] arg) {
        if ((arg.length == 0) || (arg.length > 3))
            usage();
        String attr = null;
        String query = null;

        switch (arg.length) {
        case 1:
            query = arg[0];
            break;
        case 2:
            if (arg[0].equals("-f")) {
                query = readFile(arg[1]);
            } else {
                attr = arg[0];
                query = arg[1];
            }
            break;
        case 3:
            attr = arg[0];
            query = readFile(arg[2]);
            break;
        default:
            usage();
        }
        System.out.println("\n=== Raw query is:\n["+ query + "]");

        String dirName = System.getProperty("emulator.root");
        if (dirName != null) {
            File f = new File(dirName);
            //AttributeTable.getDebugInstance(f);
        }
        // Set Table Name to make queries easier to read.
        try {
            Table[] allTables = RootNamespace.getInstance().getTables();
            for (int i = 0; i < allTables.length; i++) {
                Table table = allTables[i];
                DerbyCache.assignUniqueTableName(table);
            }
        } catch (EMDConfigException e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();

        try {
            QueryConvert converter = null;

            if (attr == null) {
                System.out.println("Running a simple query");
                converter = new QueryConvert(query, (NewObjectIdentifier)null, 
                                             (ArrayList)null);
            } else {
                System.out.println("Running a select unique");
                converter = new QueryConvert(attr, query, (String)null);
            }

            converter.convert(sb);
            System.out.println("\n=== Converted query is:\n"+sb);
        } catch (EMDException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
