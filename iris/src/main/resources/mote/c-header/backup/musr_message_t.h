/*
 * The header file is for the DBR experiment: CLM-MUSR.
 * 
 * In this experiment:
 * 	1. The onboard mote broadcasts PUSH msg
 * 	2. Once onboard mote receiveds the PUSH msgs, they log the msgs with LQ into flash.
 *  3. BS log the PUSH msgs, add LQ and then send the msgs to PC

 * 
 * The header file defines all ths msg structs used in the experiment.  
 * 
 * 
 * 
 * 
 */


/**
 * @author Songwei Fu
 */




enum {
  AM_MUSR_MESSAGE_T = 40,
  UAV_ONBOARD_MOTE_ID = 100,

  
  PUSH_PERIOD = 10,
  
  
  
};


enum {
  INIT = 1,
  PUSH = 2, 
};

typedef nx_struct {
	nx_uint8_t experimentID;
//	nx_uint8_t sourceID;
//	nx_uint16_t destinationID;
	nx_uint8_t msgType;
}MUSR_HEADER;

typedef nx_struct{
  nx_int16_t RSSI;
  nx_uint16_t LQI;
  nx_uint16_t CRC;
  nx_uint16_t noiseFlr;  

}LQ_METRICS;


typedef nx_struct{
	nx_uint16_t seqNumberPush;
	LQ_METRICS lqMetricPush; 
	nx_uint16_t tempUAV;
	nx_uint16_t humidUAV;	
}PUSH_MSG;

typedef nx_struct{
	nx_uint16_t pushNumber;
	nx_uint8_t txPowerUAV;
}INIT_MSG;

typedef nx_struct musr_message_t{
	MUSR_HEADER header;
	nx_union {
		INIT_MSG initMsg;
//		POLL_MSG pollMsg;
		PUSH_MSG pushMsg;
//		RESP_MSG respMsg;
	}body;
}musr_message_t;





typedef nx_struct {
	nx_uint8_t experimentID_log;
	nx_uint16_t seqNumberPush_log; 		
	LQ_METRICS pushMsgLQ_log;
	nx_uint8_t txPower_log;
	nx_uint16_t tempGMote_log;
	nx_uint16_t humidGMote_log;  
 }PUSH_MESSAGE_LOG;


typedef nx_struct logentry {
    nx_uint16_t len; 
    PUSH_MESSAGE_LOG logMsgPush;
} log_entry_t;



