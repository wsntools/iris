
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHFALKNEIGH = 3,
  MESSAGE_INTERVALL = 100
};

enum {
  AM_SUS_MESSAGE_T = 3,
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
	nx_uint8_t ID; // Type 3   1 more than in std
	nx_uint8_t seq_number;
	nx_uint8_t src;
	nx_uint8_t top;
	nx_uint16_t remainingDiscoveryTime;
	nx_uint16_t prr;
	nx_uint8_t rss;
	nx_uint8_t lqi;
	nx_uint16_t nfloor;
  
 }PUSH_MESSAGE_LOG;




//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashFalkNeigh{
	nx_union member{  
		nx_int8_t instruction;
  		PUSH_MESSAGE_LOG pushMsgLog;
	}member;
} ReadFlashFalkNeigh;


#endif //READFLASH_H__