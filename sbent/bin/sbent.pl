#!/usr/bin/perl
use lib "./lib";
use JSON;
my $t =  (time)[0];

# start job and retrieve monitoring url

if ( $#ARGV lt 1 ) {
  print $#ARGV;
  print "Usage: perl mon.pl <batchAdminUrl> <jobName> [jobParams]\n";
  print "Eg   : perl mon.pl http://localhost:8080/sbent-sample loadReturn a=6,b=1\n";
  exit 1;
}

my $batchAdminUrl = $ARGV[0]; # http://localhost:8080/sbent-sample
my $batchJob = $ARGV[1]; # loadReturn
my $batchParams = $ARGV[2];

my $jobP = "'jobParameters=run.id(long)=$t,$batchParams'";
my $json_string = `curl -s -S -d $jobP $batchAdminUrl/jobs/$batchJob.json`;
my $json = decode_json($json_string);
my $url =  $json->{jobExecution}->{resource};
my $jobInfoUrl =  $json->{jobExecution}->{jobInstance}->{resource};
my $jobId = $json->{jobExecution}->{id};
my $logs = "";

print `curl -s -S $jobInfoUrl`; 
print "\n";

my @data = ('job', 'step', 'status', 'exitCode', 'read', 'write', 'commit', 'rollback', 'duration');
print sprintf( "%-20.20s%-40.20s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s\n", @data );

my $status="STARTED";
while ( $status eq "STARTED" ) {
  $json_string = `curl -s -S --connect-timeout 1200 $url`;
  $json = decode_json($json_string);
  $status= $json->{jobExecution}->{status};

  for my $step(%{$json->{jobExecution}}){
    foreach my $k (keys %$step) {
      if ( $k ne "resource" ) {
        $d = $step->{$k};
        my @data = ( $batchJob, $k, $d->{status}, $d->{exitCode},$d->{readCount}, $d->{writeCount}, $d->{commitCount}, $d->{rollbackCount}, $d->{duration});
        print sprintf( "%-20.20s%-40.40s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s", @data );
        print "\n";
      }
    }
  }
  
  # check for errors
  $json_string = `curl -s -S --connect-timeout 1200 $batchAdminUrl/logger/$jobId.json`;
  $json = decode_json($json_string);
  $logs = $json->{job}->{log};
  if ( $logs ne "" ) {  
    print "logs: $logs";
    print "--------------------------------------------------------------------------------------------------------------------------------\n";
    @data = ('job', 'step', 'status', 'exitCode', 'read', 'write', 'commit', 'rollback', 'duration');
    print sprintf( "%-20.20s%-40.20s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s%-10.10s\n", @data );
  }
  
  print "--------------------------------------------------------------------------------------------------------------------------------\n";
  sleep 5;
}

print `curl -s -S $jobInfoUrl`; 
print "\n";
