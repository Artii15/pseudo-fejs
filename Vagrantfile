# -*- mode: ruby -*-
# vi: set ft=ruby :

# Every Vagrant development environment requires a box. You can search for
# boxes at https://atlas.hashicorp.com/search.
BOX_IMAGE = "ubuntu/xenial64"
NODE_COUNT = 2

Vagrant.configure("2") do |config|

    config.vm.provider "virtualbox" do |vb|
        vb.memory = "768"
    end

    (1..NODE_COUNT).each do |nodeId|
        config.vm.define "cassandra#{nodeId}" do |subconfig|
            subconfig.vm.box = BOX_IMAGE
            subconfig.vm.hostname = "cassandra#{nodeId}"
            subconfig.vm.network :private_network, ip: "10.0.0.#{nodeId + 10}"
            subconfig.vm.network :private_network, ip: "150.254.32.#{nodeId + 10}"
        end
    end

    # Install avahi on all machines  
    config.vm.provision "shell", inline: <<-SHELL
        apt-get update
        apt-get upgrade -y
        apt-get install -y wget openjdk-8-jdk python

        if [ ! -d /home/ubuntu/apache-cassandra-3.9 ] 
        then
            wget http://ftp.piotrkosoft.net/pub/mirrors/ftp.apache.org/cassandra/3.9/apache-cassandra-3.9-bin.tar.gz
            
            tar -xvf apache-cassandra-3.9-bin.tar.gz
            rm -f apache-cassandra-3.9-bin.tar.gz
            chown -R ubuntu apache-cassandra-3.9
            chgrp -R ubuntu apache-cassandra-3.9
        else
            echo "Cassandra already installed"
        fi
    SHELL
end
