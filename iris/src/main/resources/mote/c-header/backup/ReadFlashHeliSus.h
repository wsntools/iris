
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHHELISUS = 20,
  MESSAGE_INTERVALL = 100
};

enum {

  //change it from 20 to 22, only to avoid confuse GUI with ReadFlashGndNodeSus type	
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

typedef nx_struct{
	nx_uint8_t experimentID_log;
	nx_uint8_t sourceID_log;	
	nx_uint8_t seqNumberResp_log;
	LQ_METRICS respMsgLQ_log;
	nx_uint16_t tempUAV_log;
	nx_uint16_t humidUAV_log;		
}RESP_MESSAGE_LOG; 





//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    RESP_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashHeliSus{
	nx_union member{  
		nx_int8_t instruction;
  		RESP_MESSAGE_LOG respMsgLog;
	}member;
} ReadFlashHeliSus;


#endif //READFLASH_H__