#ifndef TEST_STRUCTINUNION
#define TEST_STRUCTINUNION

enum {
  AM_TEST_STRUCTINUNION = 126
};


typedef nx_struct struct1{
  nx_int16_t value;
} struct1;

typedef nx_struct struct2{
  nx_int16_t value;
} struct2;

typedef nx_union Test_StructInUnion2{
   struct1 testItem1;
   struct2 testItem2;
} Test_StructInUnion2;

typedef nx_struct Test_StructInUnion{
   Test_StructInUnion2 testUnion;
} Test_StructInUnion;

#endif //TEST_STRUCTINUNION
