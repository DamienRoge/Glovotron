/*
 * AUTHEURS : Luong-Thi-Bien BOSSUYT & ROGE Damien
 * 
 * Ce programme recoit des instructions d'un module bluetooth HC-06 branché sur les ports TX RX et commande 2 moteurs AC par un double pont en H selon l'instruction recue.
 * 0 : moteurs arrêtés
 * 1 : moteurs dans le même sens
 * 2 : moteurs dans 2 sens différents
 * 3 : //
 * 
 */
 
int motor1Pin1 = 4; // pin 2 du L293D IC
int motor1Pin2 = 5; // pin 7 du L293D IC
int enable1Pin = 6; // pin 1 on L293D IC


int motor2Pin1 = 8; // pin 10 du L293D IC
int motor2Pin2 = 9; // pin 15 du L293D IC
int enable2Pin = 10; // pin 9 du L293D IC


void setup() {
    // sets the pins as outputs:
    pinMode(motor1Pin1, OUTPUT);
    pinMode(motor1Pin2, OUTPUT);
    pinMode(enable1Pin, OUTPUT);
    pinMode(motor2Pin1, OUTPUT);
    pinMode(motor2Pin2, OUTPUT);
    pinMode(enable2Pin, OUTPUT);
    // sets enable1Pin and enable2Pin high so that motor can turn on:
    digitalWrite(enable1Pin, HIGH);
    digitalWrite(enable2Pin, HIGH);
    // initialize serial communication at 9600 bits per second:
    Serial.begin(9600);
    Serial.println("Initialisation");

    digitalWrite(motor1Pin1, HIGH);
    digitalWrite(motor1Pin2, LOW); 
    digitalWrite(motor2Pin1, LOW);
    digitalWrite(motor2Pin2, HIGH);
        
}

void loop() {
  
    //if some date is sent, reads it and saves in state
    if(Serial.available() > 0){     
      state = Serial.read();  
     Serial.println(state);
    }
       
    // STILL
    if (state == '0' || state == 0) {
        digitalWrite(motor1Pin1, LOW);
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, LOW);
    }
    
    // FORWARD
    else if (state == '1' || state == 1) {
        digitalWrite(motor1Pin1, HIGH); 
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, HIGH);
        digitalWrite(motor2Pin2, LOW);
    }
    // LEFT
    else if (state == '2' || state == 2) {
        digitalWrite(motor1Pin1, LOW); 
        digitalWrite(motor1Pin2, HIGH); 
        digitalWrite(motor2Pin1, HIGH);
        digitalWrite(motor2Pin2, LOW);
    }
    // RIGHT
    else if (state == '3' || state == 3) {
        digitalWrite(motor1Pin1, HIGH); 
        digitalWrite(motor1Pin2, LOW); 
        digitalWrite(motor2Pin1, LOW);
        digitalWrite(motor2Pin2, HIGH);
    }

    //For debugging purpose
    //Serial.println(state);

    
}
