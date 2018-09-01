#!/bin/bash
#
# description: test07b - full-sync after member is removed
# configs: ldap-attributes, posix-groups, goun-groups, posix-groups-bushy, posix-groups-bushy-dnsearch
#
# This test directly removes a member from a provisioned group and makes sure
# that a full-sync puts the user back
#

set -o errexit
set -o pipefail
set -o nounset

ME=$(basename "$0")

# Functions for test harness: read_test_config_files, test_start, start_docker, etc
# (This also pulls in functions.bash (log_always, temp-file functions, etc)
. "$(dirname "${BASH_SOURCE[0]}")/functions-pspng-testing.bash"

###
# Pull-in shell functions for generating config and verifying provisioning
# These are usually specific for different provisioning endpoints
# And the same 
#   Config Building: output_log4j_properties, output_grouper_loader_properties
#   Hooks: test_is_starting, test_is_ending
#   Provisioning verification: validate_provisioning <group> <correct members (comma-separated, alphabetical)>
# Defines: $flavor
read_test_config_files "${1:-posix-groups}"

tweak_grouper_config() {
  add_full_sync_starter 
}


# Note that the test is starting, saves start-time, etc
test_start "$ME" "Pspng ($flavor): provisioning groups and membership changes"

################
## CONFIGURE GROUPER
## This will populate and define GROUPER_CONFIG_DIR

create_grouper_daemon_config

################
## START DOCKER

start_docker "${ME}_$flavor"


wait_for_grouper_daemon_to_be_running

create_test_folder

mark_test_folder_for_provisioning

create_group1_and_group2
add_members_to_group1 banderson agasper bbrown705
add_group1_to_group2

#make sure everything is right
await_changelog_catchup

validate_provisioning "$GROUP1_NAME" "agasper,banderson,bbrown705"
validate_provisioning "$GROUP2_NAME" "agasper,banderson,bbrown705"

#break provisioning
directly_remove_member "$GROUP1_NAME" agasper

test_step "Waiting for at least one full-sync process to start and stop"
await_full_sync

# agasper should be back into GROUP1
validate_provisioning "$GROUP1_NAME" "agasper,banderson,bbrown705"

wrap_up
assert_empty "$ERRORS" "Check for exceptions in grouper_error.log"
test_success
