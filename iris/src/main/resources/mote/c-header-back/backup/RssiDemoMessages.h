/**
 * @author Dimas Abreu Dutra
 */

#ifndef RSSIDEMOMESSAGES_H__
#define RSSIDEMOMESSAGES_H__

enum {
  AM_RSSIDEMOMESSAGES = 127
};

typedef nx_struct RssiDemoMessages{
  nx_int16_t rssi;
} RssiDemoMessages;

#endif //RSSIDEMOMESSAGES_H__
