//http://13.232.24.83/2019_projects/Office/Battery_Status/main.php
/*
 * Here for voltage devider we are using 22k and 10k, To calculate battery voltage Factor 3.2 need to multiply
 */
/*-----( Calling of Header File)-----*/
//#include<AltSoftSerial.h>
#include<SoftwareSerial.h>
#include<LiquidCrystal.h>
#include <EEPROM.h>
#include <avr/wdt.h>
#define IP "13.232.24.83"                   //Server IP that one may pi or Amazone, Here it is Amazone IP

#define D3 "Bat="   // Initial field data
#define D5 "&Status="
#define D7 "&Temp="
#define D9 "&Volt="
#define D10 "&Current="
#define Diode_Point A0
#define Battery_Point A1
#define Temperature A2
#define Factor 3.2
#define Ref_Resistor 1
#define ADC2Voltage_Factor (5.00/1023)
#define Minimum_Battery_Voltage 7.7
#define BatteryPerCal 1.2
String URL_Link = "/2019_projects/Office/Battery_Status/main.php?";      // WEB page Link to send Data and control Device
String SSID_Name = "belkin.18f" ;
String SSID_PSWD = "ERF1234_0001";

String DATA, MyIP;
String CMD, Response;
unsigned long Ptime_get = 0;
char Receive_Channel;
unsigned char CountToPush = 0,ST; 
//AltSoftSerial ESP_Serial;
SoftwareSerial ESP_Serial(8,9);
SoftwareSerial Bluetooth(2, 3);
void setup()
{
  Serial.begin(9600);
  ESP_Serial.begin(9600);
  Bluetooth.begin(9600);
  Serial.println(F("If u want to set ur SSID & PSWD"));
  Serial.println(F("Then send ur SSID followed by ',' and ur Password"));
  
  Ptime_get = millis();
  ESP_Serial.listen();
  while (millis() - Ptime_get < 10000)
  {
    if (Serial.available())
    {
      delay(1000);
      while (Serial.available())
      {
        CMD += (char)Serial.read();
      }
      SSID_Collect();
      break;
    }
  }
  ESP_Setup();
  Ptime_get = millis();
  //wdt_enable(WDTO_8S);
}
void loop()
{
 // wdt_reset();
  int ADC0=0, ADC1=0, ADC2=0,Current_IN_mA=0, BatteryPercentage=0;
  float Diode_Point_Voltage, Battery_Point_Voltage, Temp;
  for(volatile unsigned char i=0; i < 20; i++){
    ADC0 += analogRead(Diode_Point);
    ADC1 += analogRead(Battery_Point);
    ADC2 += analogRead(Temperature);
  }
  ADC0 /= 20;
  ADC1 /= 20;
  ADC2 /= 20;    
//Serial.println(ADC0);Serial.println(ADC1);Serial.println(ADC2);
  if (ESP_Serial.available())
  {
    ESP_Receive("+IPD", 200);
    if(CMD.length() > 1)
    {
      SSID_Collect();
      CMD="";
    }
  }
  if (millis() - Ptime_get >= 1000)
  {
    String Charge_Status;
    Battery_Point_Voltage = ADC1*ADC2Voltage_Factor*Factor;
    Diode_Point_Voltage = ADC0*ADC2Voltage_Factor*Factor;
    Current_IN_mA = (Battery_Point_Voltage - Diode_Point_Voltage)*1000;
    if(ADC0 > ADC1){
        Charge_Status = F("Charging");
        ST=1;
    }
    else{
        Charge_Status = F("Discharging");
        ST=0;
    }
    BatteryPercentage = (Battery_Point_Voltage - Minimum_Battery_Voltage)*100/ BatteryPerCal;  
    Temp = ADC2*ADC2Voltage_Factor*100;
    ESP_Serial.listen();
    Serial.println(Diode_Point_Voltage);
    DATA = D3 + String(BatteryPercentage) + D5 + Charge_Status + D7 + String (Temp) + D9 + String(Battery_Point_Voltage) + D10 + String(Current_IN_mA);
    Serial.println(DATA);
    Bluetooth.println("@"+String(BatteryPercentage)+"*"+String (Temp)+"*"+String(ST)+"*"+String(Battery_Point_Voltage)+"*"+String(Current_IN_mA)+"*~");
    if(CountToPush == 3){
      get_server();
      CountToPush = 0;
    }
    CountToPush++;
    Ptime_get = millis();
  }
}

/*
      This function set the ESP8266 Mode 3 And on Server
*/
void ESP_Setup()
{
  ESP_Serial.println(F("AT+CWMODE=3"));
  if (ESP_Receive("OK", 100))
    Serial.println(F("Mode Set"));
  ESP_Serial.println(F("ATE0"));
  if (ESP_Receive("OK", 100))
    Serial.println(F("Echo Stop"));
    if (EEPROM.read(1023) == 255)
    {
      EEPROM.write(1023, 0);
      ESP_Serial.println(F("AT+CWSAP=\"Pump1\",\"\",10,0"));
      if (ESP_Receive("OK", 100))
      {
        ESP_Serial.println(F("AT+RST"));
        delay(4000);
      }
    }
  ESP_Serial.println(F("AT+CIPMUX=1"));
  if (ESP_Receive("OK", 200))
    Serial.println(F("CIP MUX SET"));
  ESP_Serial.println(F("AT+CWJAP?"));
  if (ESP_Receive(F("+CWJAP:"), 2000))
  {
    Serial.println(F("Connected to Wifi"));
    ESP_Serial.println(F("AT+CIFSR"));
    if (ESP_Receive("+CIFSR:STAIP", 200))
    {
      MyIP = Response.substring(14, 26);
      Serial.print(F("My IP Is")); Serial.println( MyIP);
    }
  }
  else
  {
    Serial.println(F("Wifi not Connect"));
    Serial.println(F("Enter ur SSID and PSWD"));
  }
  ESP_Serial.println(F("AT+CIPSERVER=1,80"));
  if (ESP_Receive(F("OK"), 100))
    Serial.println(F("Server Started"));
  ESP_Serial.println(F("AT+CIPMUX=1"));
  if (ESP_Receive(F("OK"), 200))
    Serial.println(F("CIP MUX SET"));
}

/*
   This function Received the data from ESP_Module
   In this Function we can pass the Argument to cross check starswith
   If argument satisfied with startwith then it return 1 and store the string in "MSG"
*/
bool ESP_Receive(String Argument, int time_delay)
{
  unsigned long Rx_Time = millis();
  String Rx_MSG;
  while (millis() - Rx_Time < time_delay)   // it working fine with delay 520 milisec
  {
    if (ESP_Serial.available())
    {
      char Byte = ESP_Serial.read();
      Rx_MSG += Byte;
      if (Byte == '\n')
      {
        if (Rx_MSG.startsWith(F("check")))
        {
          Response += Rx_MSG;
        }
        else if (Rx_MSG.startsWith(Argument))
        {
          Response = Rx_MSG;
          if (Argument == "Content-Type")
          {
            Byte = '\0'; Rx_MSG = "";
            Rx_Time = millis();
            while (millis() - Rx_Time < 1000)
            {
              if (ESP_Serial.available())
              {
                Byte = ESP_Serial.read();
                Rx_MSG += Byte;
              }
            }
            Serial.println(Rx_MSG);
            unsigned char Count = 0;
            for (int i = 0; Rx_MSG[i] != '\0'; i++)
            {
              if (Rx_MSG[i] == '\n')
              {
                Count++;
                //                Serial.println(i);
              }
            }
            CMD = Rx_MSG.substring(2, Rx_MSG.indexOf(","));
            Serial.println(CMD);
            //            Serial.println(Count);
            return 1;
          }
          if (Argument == "<html>")
          {
            Serial.println(Rx_MSG);
            Get_Control_CMD(Rx_MSG);
          }
          else if (Argument == "+IPD")
          {
            get_data(Rx_MSG);
          }
          return 1;
        }
        else
          Rx_MSG = "";
      } Rx_Time = millis();
    }
  }
  return 0;
}

/*
   This Below function will get the Control Comand from the Received String
   This function we will call once we get return 1 from ESP_Receive()
*/

bool Get_Control_CMD(String MSG)
{
  //  String CMD_Byte;
  CMD = "";
  CMD = MSG.substring((MSG.indexOf("<html>") + 6), MSG.indexOf("</html>"));
  //  Serial.println(MSG);
  //  Serial.print(F("Command is: ")); Serial.println(CMD);
  // MSG = "";
}

boolean get_data(String MSG)
{
  CMD = "";
  Receive_Channel = MSG[5];
  CMD = MSG.substring(9, MSG.indexOf("\r\n"));
  //  Serial.print(F("Command is: ")); Serial.println(CMD);
  //  MSG = "";
}
void get_server()
{
  //  String dat=DATA;
  ESP_Serial.listen();
  unsigned char Try = 0;
  String POST = "GET " + URL_Link + DATA + " HTTP/1.0\r\n" + "Host: " + IP + "\r\n" +

                "Accept: *" + "/" + "*\r\n" + "Content-Length: " + DATA.length() + "\r\n" +

                "Content-Type: application/x-www-form-urlencoded\r\n" + "\r\n"  + "\r\n";

  ESP_Serial.println(F("AT+CIPMUX=1"));
  if (ESP_Receive(F("OK"), 200))
    Serial.println(F("OK"));
StartServer:
//  ESP_Serial.println(F("AT+CIPCLOSE=2"));
//  if(ESP_Receive("2,CLOSED", 200))
//    Serial.println(F("Chanel Close"));
//  ESP_Serial.println(F("AT+CIPSTART=2,\"TCP\",\"13.232.24.83\",80"));
  ESP_Serial.print(F("AT+CIPSTART=2,\"TCP\",\""));
  ESP_Serial.print(IP);
  ESP_Serial.println(F("\",80"));
  if (ESP_Receive(F("OK"), 200))
    Serial.println(F("OK"));
  ESP_Serial.print(F("AT+CIPSEND=2,"));
  ESP_Serial.println(POST.length());
  if (ESP_Serial.find('>'))
  {
        delay(200);                //with 250 milisec delay its working fine
    Serial.println(F("OK in send"));
    Serial.print(POST);
    ESP_Serial.print(POST);
      if (ESP_Receive("Content-Type", 1000));
  }
  else
  {
    Try++;
    if (Try < 3)
    {
      ESP_Serial.println(F("AT+CIPSTATUS"));
      delay(100);
      goto StartServer;

    }
  }  
}

void SSID_Collect()
{
  unsigned char i;
  SSID_PSWD = ""; SSID_Name = "";
  for (i = 0; CMD[i] != ','; i++)
  {
    SSID_Name += CMD[i];
  }
  SSID_PSWD = CMD.substring(i + 1);
  Serial.println(SSID_Name + SSID_PSWD);
  ESP_Serial.print(F("AT+CWJAP=\""));
  ESP_Serial.print(SSID_Name);
  ESP_Serial.print(F("\",\""));
  ESP_Serial.print(SSID_PSWD);
  ESP_Serial.println("\"");
  if (ESP_Receive("OK", 8000))
  {
    Serial.println(F("Connected to Wifi"));
  }
  else
  {
    Serial.println(F("Wifi not Connect try Again"));
    delay(3000);
  }
}
