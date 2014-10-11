#ifndef TEST_ARRAYVALUE
#define TEST_ARRAYVALUE

enum {
  AM_TEST_ARRAYVALUE = 121
};

typedef nx_struct Test_ArrayValue{
  nx_uint16_t value16[5];
} Test_ArrayValue;

#endif //TEST_ARRAYVALUE
