#ifndef TEST_ARRAYVALUES
#define TEST_ARRAYVALUES

enum {
  AM_TEST_ARRAYVALUES = 123
};

typedef nx_struct Test_ArrayValues{
  nx_int8_t value8[5];
  nx_int16_t value16[5];
  nx_int32_t value32[5];
  nx_int64_t value64[5];
  nx_uint8_t uvalue8[5];
  nx_uint16_t uvalue16[5];
  nx_uint32_t uvalue32[5];
  nx_uint64_t uvalue64[5];
} Test_ArrayValues;

#endif //TEST_ARRAYVALUES
