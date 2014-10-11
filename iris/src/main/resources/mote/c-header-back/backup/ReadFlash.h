
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASH = 40,
  MESSAGE_INTERVALL = 100
};

typedef nx_struct ReadFlash{
  nx_int8_t instruction;
} ReadFlash;

#endif //READFLASH_H__