#!/bin/bash
echo "########################Starting to execute SH script...########################"
USER_NAME='cassandra'
PASSWORD='cassandra'

while ! cqlsh cassandra -u "${USER_NAME}" -p "${PASSWORD}" -e 'describe cluster' ; do
     echo "########################Waiting for main instance to be ready...########################"
     sleep 1
done

for cql_file in ./tmp/cql/*.cql;
do
  cqlsh cassandra -u "${USER_NAME}" -p "${PASSWORD}" -f "${cql_file}" ;
  echo "########################Script ""${cql_file}"" executed!!!########################"
done
echo "########################Execution of SH script is finished!########################"
echo "########################Stopping temporary instance!########################"