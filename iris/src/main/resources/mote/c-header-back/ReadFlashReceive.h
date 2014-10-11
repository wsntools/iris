
#ifndef READFLASHRECEIVE_H__
#define READFLASHRECEIVE_H__

#include "data.h"

enum {
  MESSAGE_INTERVALL = 100
};



//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    READFLASHCONTENT logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashReceive{
	nx_union member{  
		nx_int8_t instruction;
  		READFLASHCONTENT pushMsgLog;
	}member;
} ReadFlashReceive;


#endif //READFLASHRECEIVE_H__