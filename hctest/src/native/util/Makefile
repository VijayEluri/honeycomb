#
# $Id: Makefile 11444 2007-08-29 02:21:47Z pc198268 $
#
# Copyright � 2008, Sun Microsystems, Inc.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#
#   # Redistributions of source code must retain the above copyright
# notice, this list of conditions and the following disclaimer.
#
#   # Redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution.
#
#   # Neither the name of Sun Microsystems, Inc. nor the names of its
# contributors may be used to endorse or promote products derived from
# this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
# IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
# TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
# PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
# OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
# PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
# LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
# NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.



#
# Make the hctest utilities library
#

NATIVE_ROOT := $(shell cd ..;pwd)
include $(NATIVE_ROOT)/build/Defs.mk

all: build parsedate

build: $(UTIL_O_FILES:%=$(HC_HCTEST_BUILD_LIB_DIR)$(FILE_SEP)%)

.PHONY: parsedate clean

#Compile 
$(HC_HCTEST_BUILD_LIB_DIR)$(FILE_SEP)%.$(CC_OBJ_SUFFIX): %.c
	$(MKDIR) -p $(dir $@)
	$(CC) \
	$(CC_INCLUDE_FLAG)$(HC_CLIENT_BUILD_INCLUDE_DIR) $(CC_INCLUDE_FLAG)$(HC_HCTEST_UTIL_SRC_DIR) \
	$(CC_INCLUDE_FLAG)$(HC_HCTEST_COMMON_SRC_DIR) \
	$(CC_INCLUDE_FLAG)$(HC_CLIENT_BUILD_INCLUDE_DIR)$(FILE_SEP)..$(FILE_SEP)..$(FILE_SEP)curl$(FILE_SEP)src$(FILE_SEP)lib \
	$(CC_FLAGS)$@ $<

parsedate: 
	cd parsedate && $(MAKE)

clean:
	cd parsedate && $(MAKE) $@
