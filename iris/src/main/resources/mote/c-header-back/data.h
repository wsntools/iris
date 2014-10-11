#ifndef READFLASHDATA_H__
#define READFLASHDATA_H__

#define VOLUME_TO_DUMP VOLUME_CONFLOG


enum {
  AM_READFLASHRECEIVE = 20,
};

// data without the leading length field
typedef nx_struct {
	nx_uint8_t experimentID_log;
	nx_uint16_t seqNumberPush_log; 		
 	nx_uint16_t temp_log;
 	nx_uint16_t humid_log; 
 } READFLASHCONTENT;

#endif //READFLASHDATA_H__
 
 //PUSH_MESSAGE_LOG