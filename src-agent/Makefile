# if there is Makefile.local file then include it
ifneq (, $(wildcard Makefile.local))
include Makefile.local
endif

# Source lists
LIBNAME=overAgent
SOURCES=overAgent.cpp

#set compiler
#CXX=gcc 

# Object files needed to create library
OBJECTS=$(SOURCES:%.cpp=%.o)
# Library name and options needed to build it
UNAME := $(shell uname)
ifeq ($(UNAME), Linux)
LIBRARY=lib$(LIBNAME).so
endif
ifeq ($(UNAME), Darwin)
LIBRARY=lib$(LIBNAME).jnilib
endif


# Building a shared library
LINK_SHARED=$(LINK.cpp) -shared -o $@

# GNU Compiler options needed to build it
COMMON_FLAGS=-fPIC
# Options that help find errors
COMMON_FLAGS+= -W -Wall -Wextra -O3 -Wno-unused-parameter

CFLAGS += $(COMMON_FLAGS)

#CFLAGS += -I/home/ansalond/Software/java/jdk1.6.0_32/include -I/home/ansalond/Software/java/jdk1.6.0_32/include/linux
CFLAGS += -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

# add debugging output
ifeq ($(DEBUG), TRUE)
CFLAGS += -DDEBUG
endif

CXXFLAGS = $(CFLAGS)
CXXFLAGS += -m32 -L/usr/lib

all: $(LIBRARY) 

# Build native library
$(LIBRARY): $(OBJECTS)
	$(LINK_SHARED) $(OBJECTS) $(LIBRARIES)

# Cleanup the built bits
clean:
	rm -f $(LIBRARY) $(OBJECTS)

debug:
	$(MAKE) DEBUG=TRUE
