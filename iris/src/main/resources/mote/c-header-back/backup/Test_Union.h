#ifndef TEST_UNION
#define TEST_UNION

enum {
  AM_TEST_UNION = 125
};

typedef nx_union Test_Union2{
  nx_int16_t value1;
  nx_int8_t value2;
} Test_Union2;

typedef nx_struct Test_Union{
  Test_Union2 test;
}Test_Union;

#endif //TEST_UNION
