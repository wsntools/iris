/*
 * The header file is for the DBR experiment: CLM-MUSS.
 * 
 * In this experiment:
 * 	1. Java program sends  INIT cmd to ground node
 * 	2. Once ground node receives the INIT cmd, it broadcasts to UAV onboard mote with PUSH msgs.And it sends back the PUSH msg to PC for GPS timestamping.
 *  3. Once onboard mote receiveds the PUSH msgs, they log the msgs with LQ into flash.

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
  AM_MUSS_MESSAGE_T = 31,
  UAV_ONBOARD_MOTE_ID = 100,
  
  PUSH_PERIOD = 10,

  
};

enum {
  INIT = 1,
  PUSH = 2, 
//  POLL = 3,
//  RESP =4	
};


typedef nx_struct {
	nx_uint8_t experimentID;
//	nx_uint8_t sourceID;
//	nx_uint16_t destinationID;
	nx_uint8_t msgType;
}MUSS_HEADER;

typedef nx_struct{
  nx_int16_t RSSI;
  nx_uint16_t LQI;
  nx_uint16_t CRC;
  nx_uint16_t noiseFlr;  

}LQ_METRICS;

typedef nx_struct{
	nx_uint16_t pushNumber; //0xffff means forever 
//	nx_uint8_t pollNumber;
//	nx_uint16_t pollID;
//	nx_uint8_t respNumber;
//	nx_uint8_t txPowerUAV;
	nx_uint8_t txPowerGMote;
}INIT_MSG;

//typedef nx_struct{
//	nx_uint16_t pollID;	
//	nx_uint8_t totalPollNumber;
//	nx_uint8_t respNumber;
//	nx_uint8_t txPowerGMote;
//}POLL_MSG;
//
typedef nx_struct{
	nx_uint16_t seqNumberPush; 
	LQ_METRICS lqMetricPush;
	nx_uint16_t temperature;
	nx_uint16_t humidity; 		
}PUSH_MSG;
//
//typedef nx_struct{
//	nx_uint8_t seqNumberResp;
//	LQ_METRICS pushMsgLQ;
//	LQ_METRICS respMsgLQ;	
//}RESP_MSG;


//SUS radio message struct
typedef nx_struct muss_message_t{
	MUSS_HEADER header;
	nx_union {
		INIT_MSG initMsg;
//		POLL_MSG pollMsg;
		PUSH_MSG pushMsg;
//		RESP_MSG respMsg;
	}body;
}muss_message_t;





typedef nx_struct {
	nx_uint8_t experimentID_log;
	nx_uint16_t seqNumberPush_log; 		
	LQ_METRICS pushMsgLQ_log;
	nx_uint8_t txPower_log;
    nx_uint16_t temp_log;
    nx_uint16_t humid_log;  
 }PUSH_MESSAGE_LOG;

//SUS ground node log message struct
typedef nx_struct logentry {
    nx_uint16_t len; 
    PUSH_MESSAGE_LOG logMsg;
} log_entry_t;


