package org.openiot.gsn.simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class SimulationResult {
   
   private static boolean simulate = false;
   
   private static Writer  addJustProducedFromDummyDataSourceStream;
   
   private static Writer  addJustBeforeStartingToEvaluatedQueriesStream;
   
   private static Writer  addJustAfterFinishingToEvalutedRegisteredQueriesStream;
   
   private static int     counter;
   
   public static void addJustProducedFromDummyDataSource ( ) {
      if ( !simulate ) return;
      counter++;
      try {
         addJustProducedFromDummyDataSourceStream.write( new StringBuffer( ).append( System.currentTimeMillis( ) ).append( "\n" ).toString( ) );
         addJustProducedFromDummyDataSourceStream.flush( );
      } catch ( IOException e ) {
         e.printStackTrace( );
      }
   }
   
   public static void addJustBeforeStartingToEvaluateQueries ( ) {
      addJustBeforeStartingToEvaluateQueries( System.currentTimeMillis( ) );
   }
   
   public static void addJustBeforeStartingToEvaluateQueries ( long i ) {
      if ( !simulate ) return;
      try {
         addJustBeforeStartingToEvaluatedQueriesStream.write( new StringBuffer( ).append( i ).append( "\n" ).toString( ) );
         addJustBeforeStartingToEvaluatedQueriesStream.flush( );
      } catch ( IOException e ) {
         e.printStackTrace( );
      }
   }
   
   public static void initialize ( int streamElementSize , int streamOutputRate , double burstProbability , int burstMaxSize , int countPeriod , int countNo ) {
      simulate = true;
      File addJustProducedFromDummyDataSourceFile = new File( "streamProduced-StreamElementSize_is_" + streamElementSize + " StreamOutputRate_is_" + streamOutputRate + " burstProbability_is_"
         + burstProbability + " burstMaxSize_is_" + burstMaxSize + " countPeriod_is" + countPeriod + " countNo_is_" + countNo + ".octave" );
      File addJustBeforeStartingToNotifyTheRemoteClientsFile = new File( "queryProcessingStarted-StreamElementSize_is_" + streamElementSize + " StreamOutputRate_is_" + streamOutputRate
         + " burstProbability_is_" + burstProbability + " burstMaxSize_is_" + burstMaxSize + " countPeriod_is_" + countPeriod + " countNo_is_" + countNo + ".octave" );
      File addJustAfterFinishingToNotifyTheRemoteClientsFile = new File( "queryProcessingFinished-StreamElementSize_is_" + streamElementSize + " StreamOutputRate_is_" + streamOutputRate
         + " burstProbability_is_" + burstProbability + " burstMaxSize_is_" + burstMaxSize + " countPeriod_is_" + countPeriod + " countNo_is_" + countNo + ".octave" );
      try {
         addJustProducedFromDummyDataSourceFile.createNewFile( );
         addJustBeforeStartingToNotifyTheRemoteClientsFile.createNewFile( );
         addJustAfterFinishingToNotifyTheRemoteClientsFile.createNewFile( );
         addJustProducedFromDummyDataSourceStream = new FileWriter( addJustProducedFromDummyDataSourceFile );
         addJustBeforeStartingToEvaluatedQueriesStream = new FileWriter( addJustBeforeStartingToNotifyTheRemoteClientsFile );
         addJustAfterFinishingToEvalutedRegisteredQueriesStream = new FileWriter( addJustAfterFinishingToNotifyTheRemoteClientsFile );
      } catch ( Exception e ) {
         e.printStackTrace( );
      }
   }
   
   public static void addJustQueryEvaluationFinished ( long i ) {
      if ( !simulate ) return;
      try {
         addJustAfterFinishingToEvalutedRegisteredQueriesStream.write( new StringBuffer( ).append( System.currentTimeMillis( ) ).append( "\t" ).append( i ).append( "\n" ).toString( ) );
         addJustAfterFinishingToEvalutedRegisteredQueriesStream.flush( );
      } catch ( IOException e ) {
         e.printStackTrace( );
      }
      
   }
}
