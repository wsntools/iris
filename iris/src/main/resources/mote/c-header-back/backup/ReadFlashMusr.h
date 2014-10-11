
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHMUSR = 40, //to read MUSR msg log
  MESSAGE_INTERVALL = 100
};

enum {
  AM_SUS_MESSAGE_T = 20,
  AM_MUSS_MESSAGE_T = 30,
  AM_MUSR_MESSAGE_T = 40,
  
  UAV_ONBOARD_MOTE_ID = 100,
  

  
};


typedef nx_struct{
  nx_int16_t RSSI;
  nx_uint16_t LQI;
  nx_uint16_t CRC;
  nx_uint16_t noiseFlr;  

}LQ_METRICS;

//typedef nx_struct {
//	nx_uint8_t experimentID_log;
//	nx_uint16_t seqNumberPush_log; 		
//	LQ_METRICS pushMsgLQ_log;
//	nx_uint8_t txPower_log;
//	  
// }PUSH_MESSAGE_LOG;

typedef nx_struct {
	nx_uint8_t experimentID_log;
	nx_uint16_t seqNumberPush_log; 		
	LQ_METRICS pushMsgLQ_log;
	nx_uint8_t txPower_log;
	nx_uint16_t tempGMote_log;
	nx_uint16_t humidGMote_log;  
 }PUSH_MESSAGE_LOG;


//the log msg struct for the motes from where the msg should be read
typedef nx_struct logentry {
    nx_uint16_t len;
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashMusr{
	nx_union member{  
		nx_int8_t instruction;
  		PUSH_MESSAGE_LOG pushMsgLog;
	}member;
} ReadFlashMusr;


#endif //READFLASH_H__