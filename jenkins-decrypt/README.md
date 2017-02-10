
# Jenkins Decrypt

This script decrypts Jenkins secrets.

## Reference

Based on scripts at: -

- <http://xn--thibaud-dya.fr/jenkins_credentials.html>
- <https://github.com/tweksteen/jenkins-decrypt/blob/master/decrypt.py>
- <https://gist.github.com/menski/8f9980999ed43246b9b2>
- <https://www.defcon.org/images/defcon-22/dc-22-presentations/Kelley-Anderson/DEFCON-22-Kyle-Kelley-Greg-Anderson-Is-this-your-pipe-Updated.pdf>

## Setup

~~~
brew install libxml2

pip install -r requirements.txt
~~~

## Copy files from Jenkins server

If this script is not ran on the Jenkins master directly the needed files can be copy as follows: -

### jenkins-server

Tested using Jenkins vagrant server <https://github.com/jpnewman/jpnewman_ansible_jenkins/tree/master/Vagrantfiles/MiniStack>.

> In this example ```jenkins-server``` is defined in SSH config file ```./.ssh_config```.

~~~
mkdir -p ./_TESTDATA/jenkins-server
pushd ./_TESTDATA/jenkins-server
~~~

Get Jenkins root XML files and secrets

~~~
scp -F ../../.ssh_config "jenkins-server:/var/lib/jenkins/*.xml" .
scp -F ../../.ssh_config "jenkins-server:/var/lib/jenkins/secret.*" .

ssh -F ../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/identity.key.enc" > identity.key.enc
~~~

Get file permission protected secrets

~~~
mkdir -p secrets && pushd secrets
ssh -F ../../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/secrets/master.key" > master.key
ssh -F ../../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/secrets/hudson.util.Secret" > hudson.util.Secret

ssh -F ../../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/secrets/initialAdminPassword" > initialAdminPassword
ssh -F ../../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/secrets/jenkins.model.Jenkins.crumbSalt" > jenkins.model.Jenkins.crumbSalt
ssh -F ../../../.ssh_config jenkins-server "sudo cat /var/lib/jenkins/secrets/jenkins.security.ApiTokenProperty.seed" > jenkins.security.ApiTokenProperty.seed
popd
~~~

Get users

~~~
mkdir -p users && pushd users
scp -F ../../../.ssh_config -r "jenkins-server:/var/lib/jenkins/users/*" .
popd
~~~

~~~
popd
~~~

## Run script

~~~
chmod +x decrypt_secrets.py
~~~

~~~
pushd ./_TESTDATA/jenkins-server/
~~~

~~~
python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file hudson.scm.CVSSCM.xml
~~~

~~~
popd
~~~

## Checks BCrypt string

Checks if the given string matches any of the BCrypt hashes

~~~
python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret "#jbcrypt:\$2a\$10\$VfvBCZOJOp0irWCJhn66iuulFRM4UGN83T79gdHBqOmgdBoEUNEEe" --string-to-test "6251015cc32140669658a3bd9fb8509c" --debug
~~~
> - Dollar signs need to be escaped. i.e. ```$``` -> ```\$```.
> - In this example the hashed string is from file ```./_TESTDATA/users/admin/config.xml``` and the password is from file ```./_TESTDATA/secrets/initialAdminPassword```.

## Run on all xml files

~~~
find . -name "*.xml" -type f -exec python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file {} \;
~~~

## Run on all xml files, with maxdepth

~~~
find . -name "*.xml" -type f -maxdepth 1 -exec python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file {} \;
~~~

> Argument ```maxdepth``` helps restrict find so that job XML files, etc. are not parsed and it can be remove if needed.

### Run on all xml files, with maxdepth and output to log

~~~
find . -name "*.xml" -type f -maxdepth 1 -exec python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file {} \; | tee ../```basename $PWD```.txt
~~~

> Argument ```maxdepth``` helps restrict find so that job XML files, etc. are not parsed and it can be remove if needed.

### Run on all xml files, with arguments ```string-to-test```

~~~
find . -name "*.xml" -type f -exec python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file {} --string-to-test "6251015cc32140669658a3bd9fb8509c" \;
~~~

### Run, debug

~~~
python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret --xml-file users/jenkins.admin/config.xml --debug
~~~

## Decrypt String

~~~
python ../../decrypt_secrets.py secrets/master.key secrets/hudson.util.Secret "7M9ASMSUeskFqKx/iSWZgwHQQ1Gqx8sGDmP1WX2QC/4="
~~~

## Decrypt String, Groovy Jenkins Script Console

- <http://stackoverflow.com/questions/25547381/what-password-encryption-jenkins-is-using>
- <http://stackoverflow.com/questions/37683143/extract-passphrase-from-jenkins-credentials-xml>
- <https://gist.github.com/tuxfight3r/eca9442ff76649b057ab>
