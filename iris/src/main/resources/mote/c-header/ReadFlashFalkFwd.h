
#ifndef READFLASH_H__
#define READFLASH_H__

enum {
  AM_READFLASHFALKFWD = 2,
  MESSAGE_INTERVALL = 100
};

enum {
  AM_SUS_MESSAGE_T = 2,
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
	nx_uint8_t ID; // Type 2   12 more than in std
	nx_uint8_t source_id;
	nx_uint8_t dest_id;
	nx_uint16_t seq_number;
	nx_uint8_t total_num_pkts;
	nx_uint8_t batch_id;
	nx_uint8_t strategy;
	nx_uint16_t noOfDiscoveryPkts;
	nx_uint8_t prr0;
	nx_uint8_t rssi0;
	nx_uint8_t lqi0;
	nx_uint8_t hop_count;
	nx_uint8_t RSSI_min;
	nx_uint8_t RSSI_max;
	nx_uint8_t LQI_min;
	nx_uint8_t LQI_max;
	nx_uint8_t RSSI_ave;
	nx_uint8_t LQI_ave;
	nx_uint16_t timeStamp;
	nx_uint16_t nfloor;
  
 }PUSH_MESSAGE_LOG;




//the log msg struct for motes
typedef nx_struct logentry {
    nx_uint16_t len;
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


//the msg struct used for the readflash app
typedef nx_struct ReadFlashFalkFwd{
	nx_union member{  
		nx_int8_t instruction;
  		PUSH_MESSAGE_LOG pushMsgLog;
	}member;
} ReadFlashFalkFwd;


#endif //READFLASH_H__