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




import java.util.ArrayList;

import com.sun.honeycomb.client.NameValueObjectArchive;

public class DeleteStress implements Runnable
{
    private static void printUsage()
    {
        System.err.println("NAME");
        System.err.println("       DeleteStress - Stress Honeycomb delete ops");
        System.err.println();
        System.err.println("SYNOPSIS");
        System.err.println("       java DeleteStress <dataVIP> <num_threads> <runtime_seconds>");
        System.err.println();
        System.err.println("DESCRIPTION");
        System.err.println("       Stress Honeycomb delete operations.");
        System.err.println("       Reads OIDs from STDIN.  Terminates after reading EOF or when time limit is reached.");
        System.err.println();
        System.err.println("ARGS");
        System.err.println("       dataVIP - Host or IP for data requests.");
        System.err.println("       num_threads - Number of concurrent threads.");
	System.err.println("       runtime_seconds - How long to run. -1 means all inputted OIDs are deleted.");
        System.err.println();
        System.err.println("EXAMPLES");
        System.err.println("       java DeleteStress data1 10 -1");
    }
    
    public static void main(String [] args) throws Throwable
    {
        if (args.length != 3) {
            printUsage();
            System.err.println("error: insufficient arguments");
            System.exit(1);
        }

        String dataVIP = null;
        int numThreads = 0;
	long runtimeMillis = 0;

        dataVIP=args[0];

        try {
            numThreads = Integer.parseInt(args[1]);
            if (numThreads < 0) {
                throw new Throwable();
            }
        }
        catch (Throwable t) {
            printUsage();
            System.err.println("error: invalid argument: num_threads: " + args[1]);
            System.exit(1);
        }

	try {
	  runtimeMillis = Long.parseLong(args[2]) * 1000;
	}
	catch (Throwable t) {
	  printUsage();
	  System.err.println("error: invalid argument: runtime_seconds: " + args[2]);
	  System.exit(1);
	}

        DeleteStress deleteStress = new DeleteStress(dataVIP, numThreads, runtimeMillis);
        deleteStress.run();
    }

  private String dataVIP;
  private int numThreads;
  private long runtimeMillis;

    public DeleteStress(String dataVIP, 
                          int numThreads,
			long runtimeMillis)
        throws Throwable
    {
        this.dataVIP=dataVIP;
        this.numThreads=numThreads;
	this.runtimeMillis = runtimeMillis;
    }

    public void run() 
    {


        long sleepTime = 1000; // msecs
        long timeout = 60000; //msec
        NameValueObjectArchive archive = null;
        long t0 = System.currentTimeMillis();

        while (archive==null && (System.currentTimeMillis() - t0) < timeout) {

            try {
                Queue oids = new Queue(numThreads);

                archive = new NameValueObjectArchive(dataVIP);

	        int connectionTimeout = 600000; // 10 minutes in ms
	        int socketTimeout = 600000; // 10 minutes in ms

	        archive.setConnectionTimeout(connectionTimeout);
	        archive.setSocketTimeout(socketTimeout);

	        ReadFile readFile = new ReadFile(oids,numThreads,runtimeMillis);
	        readFile.start();

                ArrayList threads = new ArrayList();
            
                for (int i = 0; i < numThreads; i++) {
                    DeleteThread deleteThread = new DeleteThread(archive, runtimeMillis, oids);
                    threads.add(deleteThread);
                }
            
                for (int i = 0; i < numThreads; i++) {
                    ((DeleteThread) threads.get(i)).start();
                }


                for (int i = 0; i < numThreads; i++) {
                    ((DeleteThread) threads.get(i)).join();
                }

	        readFile.cease();
            }
            catch (Throwable throwable) {
                System.err.println("An unexpected error occurred.");
                throwable.printStackTrace();
                try { Thread.sleep(sleepTime); } catch (Throwable t) {}
            }
        }
    }
}
