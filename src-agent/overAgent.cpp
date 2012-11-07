/**
 * @file    overAgent.cpp
 * @brief	overAgent HPC profiler for Java
 *
 * @author	Achille Peternier (C) USI 2010, achille.peternier@gmail.com
 */



//////////////
// #INCLUDE //
//////////////
#include <unistd.h>
#include <syscall.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jvmti.h>
#include "overAgent.h"



////////////
// GLOBAL //
////////////

// Global global:
GlobalAgentData gdata;



////////////
// STATIC //
////////////

// Local global:
static jvmtiEnv *jvmti = NULL;
static jvmtiCapabilities capa;

////////////
// COMMON //
////////////

/**
 * Error checking procedure.
 */
void check_jvmti_error(jvmtiEnv *jvmti, jvmtiError errnum, const char *str)
{
	if ( errnum != JVMTI_ERROR_NONE )
	{
		char *errnum_str;
		errnum_str = NULL;

		jvmti->GetErrorName(errnum, &errnum_str);
		printf("ERROR: JVMTI: %d(%s): %s\n", errnum, (errnum_str==NULL?"Unknown":errnum_str), (str==NULL?"":str));
	}
}



//////////
// SYNC //
//////////

static void enter_critical_section()
{
	jvmtiError error;
	error = jvmti->RawMonitorEnter(gdata.lock);
	check_jvmti_error(jvmti, error, "Cannot enter with raw monitor");
}

static void exit_critical_section()
{
	jvmtiError error;

	error = jvmti->RawMonitorExit(gdata.lock);
	check_jvmti_error(jvmti, error, "Cannot exit with raw monitor");
}



///////////////
// CALLBACKS //
///////////////
static void lazyInit(JNIEnv* jni_env) {

	// Send notification:
	if(gdata.overAgentClass == NULL) {
		jclass localRef = jni_env->FindClass("deferredmethods/Agent");
		if (localRef == NULL)
		{
			printf("Class not found\n");
			return;
		}
		/* Create a global reference */
		gdata.overAgentClass = (jclass) jni_env->NewGlobalRef(localRef);

		if(gdata.overAgentClass == NULL)
		{
			printf("Error creating global reference\n");
			return;
		}

//                        /* The local reference is no longer useful */
//                        jni_env->DeleteLocalRef(localRef);


		// Get thread end Java callback:
		jmethodID localMethodID = jni_env->GetStaticMethodID(gdata.overAgentClass, "onThreadDeath", "()V");
		if (localMethodID == NULL)
		{
			printf("Method not found\n");
			return;
		}
		gdata.threadEndCB = localMethodID;

		// Get VM death Java callback:
		localMethodID = jni_env->GetStaticMethodID(gdata.overAgentClass, "onVMDeath", "()V");
		if (localMethodID == NULL)
		{
			printf("Method not found\n");
			return;
		}
		gdata.vmDeathCB = localMethodID;
	}

}

/**
 * VM Death callback
 */
static void JNICALL callbackVMDeath(jvmtiEnv *jvmti_env, JNIEnv* jni_env)
{
	enter_critical_section();
	{
		lazyInit(jni_env);
		jni_env->CallStaticVoidMethod(gdata.overAgentClass, gdata.vmDeathCB);
	}
	abort:
	exit_critical_section();
}

/**
 * Thread end
 */
static void JNICALL callbackThreadEnd(jvmtiEnv *jvmti, JNIEnv* jni_env, jthread thread)
{
	enter_critical_section();
	{
		lazyInit(jni_env);
		jni_env->CallStaticVoidMethod(gdata.overAgentClass, gdata.threadEndCB);
	}
	abort:
	exit_critical_section();
}



/////////////
// METHODS //
/////////////

/**
 * Agent entry point
 */
JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved)
{
	jvmtiError error;
	jint res;
	jvmtiEventCallbacks callbacks;

	//////////////////////////
	// Init JVMTI environment:
	res = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);
	if (res != JNI_OK || jvmti == NULL)
	{
		printf("ERROR: Unable to access JVMTI Version 1 (0x%x),"
				" is your J2SE a 1.5 or newer version?"
				" JNIEnv's GetEnv() returned %d\n",
				JVMTI_VERSION_1, res);
	}

	// Globalize it for Agent_OnUnload():
	gdata.jvmti = jvmti;

	/////////////////////
	// Init capabilities:
	memset(&capa, 0, sizeof(jvmtiCapabilities));
	capa.can_signal_thread = 1;
	capa.can_get_owned_monitor_info = 1;

	error = jvmti->AddCapabilities(&capa);
	check_jvmti_error(jvmti, error, "Unable to get necessary JVMTI capabilities.");

	//////////////////
	// Init callbacks:
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.VMDeath = &callbackVMDeath;
	callbacks.ThreadEnd = &callbackThreadEnd;

	error = jvmti->SetEventCallbacks(&callbacks, (jint)sizeof(callbacks));
	check_jvmti_error(jvmti, error, "Cannot set jvmti callbacks");

	error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, 0);
	check_jvmti_error(jvmti, error, "Cannot set jvm death hook");


	///////////////
	// Init events:
//        error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_DEATH, (jthread)NULL);
//	check_jvmti_error(jvmti, error, "Cannot set event notification");

	error = jvmti->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_THREAD_END, (jthread)NULL);
	check_jvmti_error(jvmti, error, "Cannot set event notification");

	// Here we create a raw monitor for our use in this agent to
	// protect critical sections of code:
	error = gdata.jvmti->CreateRawMonitor("agent data", &(gdata.lock));
	check_jvmti_error(jvmti, error, "Cannot create raw monitor");

	return JNI_OK;
}

/**
 * Agent exit point. Last code executed.
 */
JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm)
{
	// Conclude:
	// printf("%s quit\n", OA_NAME);
}


