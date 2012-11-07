////////////////////////////////////////////////////
//                                                //
// overAgent v0.1, Achille Peternier (C) 2010 USI //
//                                                //
////////////////////////////////////////////////////
#ifndef OVERAGENT_H_INCLUDED
#define OVERAGENT_H_INCLUDED



//////////////
// #INCLUDE //
//////////////
#include <jvmti.h>


/////////////
// #DEFINE //
/////////////

    // Generic:
    #define OA_NAME     "overAgent v0.2x"
    #define OA_VERSION  0.2


/////////////
// STRUCTS //
/////////////

// Agent global vars:
typedef struct
{
   jvmtiEnv    *jvmti;

   // Data access Lock:
   jrawMonitorID  lock;

   // Callback data:
   jclass overAgentClass; // store a **global** reference to the Agent class
   jmethodID threadEndCB; // store a reference to the "thread end" callback method of the Agent class
   jmethodID vmDeathCB; // store a reference to the "vm death" callback method of the Agent class
} GlobalAgentData;



/////////////
// METHODS //
/////////////

// Common:
void check_jvmti_error(jvmtiEnv *jvmti, jvmtiError errnum, const char *str);

#endif // OVERAGENT_H_INCLUDED
