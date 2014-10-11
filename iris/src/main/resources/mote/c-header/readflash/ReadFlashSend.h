
#ifndef READFLASHSEND_H__
#define READFLASHSEND_H__


enum {
  AM_READFLASHSEND = 20
};



//the msg struct used for the readflash app
typedef nx_struct ReadFlashSend{
    nx_int8_t instruction;
} ReadFlashSend;


#endif //READFLASHSEND_H__