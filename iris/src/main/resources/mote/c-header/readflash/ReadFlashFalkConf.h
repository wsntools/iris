
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHFALKCONF = 1,
  MESSAGE_INTERVALL = 100
};

enum {
  AM_SUS_MESSAGE_T = 1,
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
	nx_uint8_t ID; // Type 1   4 more than in std
	nx_uint8_t dest_id;
	nx_uint8_t total_num_pkts;
	nx_uint8_t batch_id;
	nx_uint16_t interPktTime;
	nx_uint8_t strategy;
	nx_uint8_t prr0min;
	nx_uint8_t rssi0min;
	nx_uint8_t lqi0min;
	//nx_uint16_t prr0max;
	//nx_uint16_t rssi0max;
	//nx_uint16_t lqi0max;
	//nx_uint16_t prr0delta;
	//nx_uint16_t rssi0delta;
	//nx_uint16_t lqi0delta;
	nx_uint16_t numberOfDiscoveryPkts[3];
	nx_uint8_t numOfDisPktsLenght;
	nx_uint16_t nfloor;
  
 }PUSH_MESSAGE_LOG;




//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashFalkConf{
	nx_union member{  
		nx_int8_t instruction;
  		PUSH_MESSAGE_LOG pushMsgLog;
	}member;
} ReadFlashFalkConf;


#endif //READFLASH_H__