#!/usr/bin/ruby
require 'rubygems'      # sudo apt-get install rubygems
require 'tenjin'        # gem install tenjin

# This script generates a set of virtual sensor description file for testing the
# local, remote and remote-rest wrappers.
#
# Usage: ruby testcase-generator.rb <LOW_ID> <HIGH_ID> <LOCAL_CONTACT_POINT> <REMOTE_CONTACT_POINT> <VSNAME> <USERNAME> <PASSWORD>
# eg.  : ruby testcase-generator.rb 1 10 "http://localhost:22011/streaming/" "http://localhost:22010/streaming/" "MEMORYMONITORVS" "tim" "tim"
#
# <LOW_ID>               :
# <HIGH_ID>              :
# <LOCAL_CONTACT_POINT>  :
# <REMOTE_CONTACT_POINT> :
# <VSNAME>               :
# <USERNAME>             :
# <PASSWORD>             :


# Get the script parameters

LOW_ID = ARGV[0]
HIGH_ID = ARGV[1]
LOCAL_CONTACT_POINT = ARGV[2]
REMOTE_CONTACT_POINT = ARGV[3]
VSNAME = ARGV[4]
USERNAME = ARGV[5]
PASSWORD = ARGV[6]

# Generate the output XML file

(LOW_ID..HIGH_ID).each { |index|
    engine = Tenjin::Engine.new()
    context = {
	    :id=>index,
	    :local_contact_point=>LOCAL_CONTACT_POINT,
	    :remote_contact_point=>REMOTE_CONTACT_POINT,
	    :vsname=>VSNAME,
            :username=>USERNAME,
            :password=>PASSWORD
    }

    # Generate the Local Wrapper Virtual Sensor Description File
    output = engine.render('templates/local-wrapper.rbxml', context)
    File.open("testcase/local_" + VSNAME.downcase + index + ".xml",'w+'){|f| f<<output }

    #Generate the Virtual Sensor embedding a Rest Remote Wrapper
    output = engine.render('templates/remoterest-wrapper.rbxml', context)
    File.open("testcase/remoterest_" + VSNAME.downcase + index + ".xml",'w+'){|f| f<<output }

    #Generate the Virtual Sensor embedding a Push Remote Wrapper
    output = engine.render('templates/remotepush-wrapper.rbxml', context)
    File.open("testcase/remotepush_" + VSNAME.downcase + index + ".xml",'w+'){|f| f<<output }
}
 


