#ifndef TEST_ARRAYMSG
#define TEST_ARRAYMSG

enum {
  AM_TEST_ARRAYMSG = 122
};

typedef nx_struct Test_ArrayStruct{
  nx_int16_t value16[5];
  nx_int32_t value32[5];
} Test_ArrayStruct;

typedef nx_struct Test_ArrayMsg{
  Test_ArrayStruct testStruct[5];
} Test_ArrayMsg;





#endif //TEST_ARRAYMSG
