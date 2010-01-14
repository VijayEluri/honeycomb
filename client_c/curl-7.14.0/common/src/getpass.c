/***************************************************************************
 *                                  _   _ ____  _
 *  Project                     ___| | | |  _ \| |
 *                             / __| | | | |_) | |
 *                            | (__| |_| |  _ <| |___
 *                             \___|\___/|_| \_\_____|
 *
 * Copyright (C) 1998 - 2005, Daniel Stenberg, <daniel@haxx.se>, et al.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution. The terms
 * are also available at http://curl.haxx.se/docs/copyright.html.
 *
 * You may opt to use, copy, modify, merge, publish, distribute and/or sell
 * copies of the Software, and permit persons to whom the Software is
 * furnished to do so, under the terms of the COPYING file.
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY
 * KIND, either express or implied.
 *
 * $Id: getpass.c 5516 2005-08-31 20:56:48Z sarnoud $
 ***************************************************************************/

/* This file is a reimplementation of the previous one, due to license
   problems. */

#include "setup.h"

#ifndef HAVE_GETPASS_R
/* this file is only for systems without getpass_r() */

#include <stdio.h>
#include <string.h>

#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif

#include "getpass.h"

#ifdef HAVE_FCNTL_H
#include <fcntl.h>
#endif
#ifdef HAVE_TERMIOS_H
#include <termios.h>
#else
#ifdef HAVE_TERMIO_H
#include <termio.h>
#endif
#endif

/* The last #include file should be: */
#if defined(CURLDEBUG) && defined(CURLTOOLDEBUG)
#include "memdebug.h"
#endif

#ifdef VMS
/* VMS implementation */
#include descrip
#include starlet
#include iodef
/* #include iosbdef */
char *getpass_r(const char *prompt, char *buffer, size_t buflen)
{
  long sts;
  short chan;

  /* MSK, 23-JAN-2004, iosbdef.h wasn't in VAX V7.2 or CC 6.4  */
  /* distribution so I created this.  May revert back later to */
  /* struct _iosb iosb;                                        */
  struct _iosb
     {
     short int iosb$w_status; /* status     */
     short int iosb$w_bcnt;   /* byte count */
     int       unused;        /* unused     */
     } iosb;

  $DESCRIPTOR(ttdesc, "TT");

  buffer[0]='\0';
  sts = sys$assign(&ttdesc, &chan,0,0);
  if (sts & 1) {
    sts = sys$qiow(0, chan,
                   IO$_READPROMPT | IO$M_NOECHO,
                   &iosb, 0, 0, buffer, buflen, 0, 0,
                   prompt, strlen(prompt));

    if((sts & 1) && (iosb.iosb$w_status&1))
      buffer[iosb.iosb$w_bcnt] = '\0';

    sts = sys$dassgn(chan);
  }
  return buffer; /* we always return success */
}
#define DONE
#endif /* VMS */

#ifdef WIN32
/* Windows implementation */
#include <conio.h>
char *getpass_r(const char *prompt, char *buffer, size_t buflen)
{
  size_t i;
  fputs(prompt, stderr);

  for(i=0; i<buflen; i++) {
    buffer[i] = getch();
    if ( buffer[i] == '\r' ) {
      buffer[i] = 0;
      break;
    }
    else
      if ( buffer[i] == '\b')
        /* remove this letter and if this is not the first key, remove the
           previous one as well */
        i = i - (i>=1?2:1);
  }
  /* if user didn't hit ENTER, terminate buffer */
  if (i==buflen)
    buffer[buflen-1]=0;

  return buffer; /* we always return success */
}
#define DONE
#endif /* WIN32 */

#ifndef DONE /* not previously provided */

#ifdef HAVE_TERMIOS_H
#define struct_term struct termios
#else
#ifdef HAVE_TERMIO_H
#define struct_term  struct termio
#else
#undef struct_term
#endif
#endif

static bool ttyecho(bool enable, int fd)
{
#ifdef struct_term
  static struct_term withecho;
  static struct_term noecho;
#endif
  if(!enable) {
  /* dissable echo by extracting the current 'withecho' mode and remove the
     ECHO bit and set back the struct */
#ifdef HAVE_TERMIOS_H
    tcgetattr(fd, &withecho);
    noecho = withecho;
    noecho.c_lflag &= ~ECHO;
    tcsetattr(fd, TCSANOW, &noecho);
#else /* HAVE_TERMIOS_H */
#ifdef HAVE_TERMIO_H
    ioctl(fd, TCGETA, &withecho);
    noecho = withecho;
    noecho.c_lflag &= ~ECHO;
    ioctl(fd, TCSETA, &noecho);
#else /* HAVE_TERMIO_H */
/* neither HAVE_TERMIO_H nor HAVE_TERMIOS_H, we can't disable echo! */
    (void)fd; /* prevent compiler warning on unused variable */
    return FALSE; /* not disabled */
#endif
#endif
    return TRUE; /* disabled */
  }
  else {
    /* re-enable echo, assumes we disabled it before (and set the structs we
       now use to reset the terminal status) */
#ifdef HAVE_TERMIOS_H
    tcsetattr(fd, TCSAFLUSH, &withecho);
#else /* HAVE_TERMIOS_H */
#ifdef HAVE_TERMIO_H
    ioctl(fd, TCSETA, &withecho);
#else
/* neither HAVE_TERMIO_H nor HAVE_TERMIOS_H */
    return FALSE; /* not enabled */
#endif
#endif
    return TRUE; /* enabled */
  }
}

char *getpass_r(const char *prompt, /* prompt to display */
                char *password,     /* buffer to store password in */
                size_t buflen)      /* size of buffer to store password in */
{
  ssize_t nread;
  bool disabled;
  int fd=open("/dev/tty", O_RDONLY);
  if(-1 == fd)
    fd = 1; /* use stdin if the tty couldn't be used */

  disabled = ttyecho(FALSE, fd); /* disable terminal echo */

  fputs(prompt, stderr);
  nread=read(fd, password, buflen);
  if(nread > 0)
    password[--nread]=0; /* zero terminate where enter is stored */
  else
    password[0]=0; /* got nothing */

  if(disabled) {
    /* if echo actually was disabled, add a newline */
    fputs("\n", stderr);
    ttyecho(TRUE, fd); /* enable echo */
  }

  if(1 != fd)
    close(fd);

  return password; /* return pointer to buffer */
}

#endif /* DONE */
#endif /* HAVE_GETPASS_R */
