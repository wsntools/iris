#ifndef TEST_SIMPLEVALUES
#define TEST_SIMPLEVALUES

enum {
  AM_TEST_SIMPLEVALUES = 120
};

typedef nx_struct Test_SimpleValues{
  nx_int8_t value8;
  nx_int16_t value16;
  nx_int32_t value32;
  nx_int64_t value64;
  nx_uint8_t uvalue8;
  nx_uint16_t uvalue16;
  nx_uint32_t uvalue32;
  nx_uint64_t uvalue64;
  
} Test_SimpleValues;

#endif //TEST_SIMPLEVALUES
