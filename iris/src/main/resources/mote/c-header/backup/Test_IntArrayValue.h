#ifndef TEST_INTARRAYVALUE
#define TEST_INTARRAYVALUE

enum {
  AM_TEST_INTARRAYVALUE = 124
};

typedef nx_struct Test_IntArrayValue{
  nx_int32_t value16[];
} Test_IntArrayValue;

#endif //TEST_INTARRAYVALUE
