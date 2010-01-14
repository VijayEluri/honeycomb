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



/*
 * Component = Mailbox service
 * Synopsis  = internal mailbox structures and constants
 */


#ifndef _HC_MB_CONF_H
#define _HC_MB_CONF_H

#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <fcntl.h>

#include "mbox.h"
#include "cmm.h"

#define MAILBOX_PATH "/mailboxes"
#define MAILBOX_MODE (S_IRUSR | S_IWUSR)
#define MAILBOX_DIR_MODE (S_IRWXU | S_IRGRP | S_IXGRP)
#define MAILBOX_VER  0x1
#define MAILBOX_TAG_MAXLEN 64

/*
 * possible flags for mailbox handle
 */
#define MB_CREATOR  0x1
#define MB_OWNER    0x2
#define MB_CALLBACK 0x4

/*
 * VALID value 
 * This is supposed to catch most common errors.
 */
#define MB_VALID  0x55AA55AA

/*
 * Mailbox handle 
 */
typedef struct {
    unsigned long valid;
    unsigned long flags;
    int           fd;
    void*         maddr;
} mb_handle_t;

/*
 * Mailbox header in shared memory
 */
typedef struct {
    unsigned long    version;
    mb_state_t       curState;
    mb_state_t       rqstState;
    char             mboxTag[MAILBOX_TAG_MAXLEN];
    mb_callback_t    usrClbk;
    long             tmStamp;
    size_t           ttSize;
} mb_hdr_t;

/* internal API */
extern int mb_net_init       (void);
extern int mb_net_disconnect (int);
extern int mb_net_publish    (void);
extern int mb_net_update     (int);
extern int mb_net_delete     (cmm_nodeid_t);
extern int mb_net_sync       (void);

#endif /* _HC_MB_CONF_H */
