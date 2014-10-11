
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHGNDNODESUS = 20,
  MESSAGE_INTERVALL = 100
};

enum {
  AM_SUS_MESSAGE_T = 20,
  UAV_ONBOARD_MOTE_ID = 100,
  
  PUSH_PERIOD = 200,
  POLL_PERIOD = 200,
  RESP_PERIOD = 200
  
};


typedef nx_struct{
  nx_int16_t RSSI;
  nx_uint16_t LQI;
  nx_uint16_t CRC;
  nx_uint16_t noiseFlr;  

}LQ_METRICS;

typedef nx_struct {
	nx_uint8_t experimentID_log;
	nx_uint16_t seqNumberPush_log; 		
	LQ_METRICS pushMsgLQ_log;
 	nx_uint16_t temp_log;
 	nx_uint16_t humid_log; 
  
 }PUSH_MESSAGE_LOG;




//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashGndNodeSus{
	nx_union member{  
		nx_int8_t instruction;
  		PUSH_MESSAGE_LOG pushMsgLog;
	}member;
} ReadFlashGndNodeSus;


#endif //READFLASH_H__