/* 
 * This file is part of BioGeomancer.
 *
 *  Biogeomancer is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  Biogeomancer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Biogeomancer; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.biogeomancer.validation;

import java.util.*;

import org.biogeomancer.records.*;
import org.biogeomancer.utils.*;

import org.gbif.datatester.*;
import org.gbif.datatester.tests.*;
import org.gbif.datatester.exception.*;


/**
 * <p>Manager class responsible for data validation.</p>
 * 
 * @author Renato De Giovanni ( renato at cria . org . br )
 * 
 */
public class ValidationManager implements RecordFilter {

/**
 * <p>Internal class to handle exceptions.</p>
 */
    public class ValidationManagerException extends Exception {

        ValidationManagerException( String msg ) {

            super( msg );
        }
        ValidationManagerException( Exception exception ) {

            super( exception );
        }
        ValidationManagerException( String msg, Exception exception ) {

            super( msg, exception );
        }
    }

/**
 * <p>Main configuration object.</p>
 */
    private Properties properties;

/**
 * <p>Original biogeomancer record set.</p>
 */
    private RecSet bgRecSet;

/**
 * <p>GenericRecordSet wrapping the BG record set.</p>
 */
    private GenericRecordSet genericRecordSet;

/**
 * <p>Indicates if the GenericRecordSet was loaded.</p>
 */
    private boolean isLoaded;

/**
 * <p>BioGeomancer known concepts.</p>
 */
    private KnownConcepts knownConcepts;

/**
 * <p>Map to store OutlierTest results. Key is Concept and value is NumericOutlierResult.</p>
 */
    private Map numericOutlierResults = new HashMap();

/**
 * <p>Another direct reference to longitude outliers just to optimize performance 
 * when displaying results.</p>
 */
    private NumericOutlierResult longOutliers;

/**
 * <p>Another direct reference to latitude outliers just to optimize performance 
 * when displaying results.</p>
 */
    private NumericOutlierResult latOutliers;

/**
 * <p>Wrapper object storing information about itinerary outlier results.</p>
 */
    private ItineraryResult itineraryResult;

/**
 * <p>Wrapper object storing information about elevation error results.</p>
 */
    private ElevationErrorResult elevationErrorResult;

/**
 * <p>Wrapper object storing information about geographic error results.</p>
 */
    private GeographicErrorResult geographicErrorResult;

/**
 * <p>List of environmental variables to be tested (used only 
 * when the environmental outlier test is active).</p>
 */
    private List<String> envVars = new ArrayList<String>();

/**
 * <p>Concepts related to each environmental variable to be tested (used only 
 * when the environmental outlier test is active).</p>
 */
    private Set<Concept> envConcepts = new HashSet<Concept>();

/**
 * <p>Wrapper object to read environmental outlier results.</p>
 */
    private EnvironmentalOutlierResult environmentalOutlierResult;

/**
 * <p>Wrapper object to read ecological outlier results.</p>
 */
    private EcologicalOutlierResult ecologicalOutlierResult;

/**
 * <p>List of record indexes that can't be used by geographic, environmental
 * and itinerary outlier tests because they have either more than one "valid" interpretation
 * or all interpretations "invalid" according to previous tests. This property is
 * only used when there are multiple interpretations associated with the same record!</p>
 */
    private List<Integer> skippedRecords = new ArrayList<Integer>();

/**
 * <p>Map (record index pointing to interpretation index) to indicate the 
 * remaining interpretation that should be tested by geographic, environmental
 * and itinerary outlier tests. This property is only used when there are multiple 
 * interpretations associated with the same record!</p>
 */
    private Map<Integer,Integer> remainingInterpretations = new HashMap<Integer,Integer>();

/**
 * <p>List of smaller record sets after partitioning the original one by scientific
 * name. So each partition contains records related to a particular species.</p>
 */
    private List<GenericRecordSet> recordSetPartitions;


/**
 * <p>Constructor.</p>
 *
 * @param properties Object containing configuration (key/value pairs).
 * @param bgRecSet BioGeomancer record set
 */
    public ValidationManager( Properties properties, RecSet bgRecSet ) {

        this.properties = properties;
        this.bgRecSet = bgRecSet;
    }

/**
 * <p>Returns the version of the DataTester framework.</p>
 *
 *
 * @return DataTester version being used
 */
    public String getDataTesterVersion() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("org.gbif.datatester.DataTester");

        String string = "?";

        try {

            string = resourceBundle.getString( "framework.version" );
        }
        catch ( MissingResourceException ignored ) { }

        return string;
    }

/**
 * <p>Indicates if validation is enabled or not.</p>
 *
 *
 * @return True if validation is enabled, false otherwise.
 */
    public boolean enabled() {

        boolean enabled = false;

        String enableValidationStr = this.properties.getProperty("Validation.enabled");

        if ( enableValidationStr != null && Boolean.parseBoolean( enableValidationStr ) ) {

            enabled = true;
        }

        return enabled;
    }

/**
 * <p>Returns the current validation target: OriginalRecords or Interpretations. 
 * Defaults to OriginalRecords.</p>
 *
 *
 * @return String "OriginalRecords", "Interpretations" or empty String if it was not set.
 *
 * @throws ValidationManager.ValidationManagerException
 */
    public String target() throws ValidationManager.ValidationManagerException {

        String validationTargetStr = this.properties.getProperty("Validation.target");

        if ( validationTargetStr == null ) {

            if ( this.enabled() ) {

                String msg = "Property 'Validation.target' was not set in the main " + 
                             "configuration file.";
                throw new ValidationManager.ValidationManagerException( msg );
            }

            return "";
        }
        else {

            if ( this.enabled() && !validationTargetStr.equals( "OriginalRecords" ) && 
                 !validationTargetStr.equals( "Interpretations" ) ) {

                String msg = "Unknown value for property 'Validation.target' in the main " + 
                             "configuration file. Should be either 'OriginalRecords' or " +
                             "'Interpretations'.";
                throw new ValidationManager.ValidationManagerException( msg );
            }
        }

        return validationTargetStr;
    }

/**
 * <p>Loads a DataTester GenericRecordSet based on the Biogeomancer RecSet. If
 * the environmental outlier test is active then first it loads environmental
 * data in the BG record structure.</p>
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void loadRecordSet() throws ValidationManager.ValidationManagerException {

        if ( ! this.isLoaded ) {

            try {

                // Load environmental data associated with each point if needed
                this.loadEnvironmentalData();

                // Variable to hold the set of all known concepts
                Set concepts;

                // Instantiate validation record set according to the target:
                // OriginalRecords or Interpretations
                // (Factory functionality)
                if ( this.target().equals( "OriginalRecords" ) ) {

                    concepts = new HashSet( OriginalRecordSet.CONCEPTS );

                    // Generic record set needs to know in advance the universe of concepts
                    if ( this.envConcepts.size() > 0 ) {

                        concepts.addAll( this.envConcepts );
                    }

                    this.genericRecordSet = new OriginalRecordSet( bgRecSet, concepts );
                }
                else {

                    concepts = new HashSet( InterpretationRecordSet.CONCEPTS );

                    // Generic record set needs to know in advance the universe of concepts
                    if ( this.envVars.size() > 0 ) {

                        concepts.addAll( this.envConcepts );
                    }

                    this.genericRecordSet = new InterpretationRecordSet( bgRecSet, concepts );
                }

                // All tests will need this
                this.knownConcepts = new KnownConcepts( concepts );
            }
            catch ( DataTesterException exception ) {

                throw new ValidationManager.ValidationManagerException( exception.getMessage() );
            }

            this.isLoaded = true;
        }
    }

/**
 * <p>Loads environmental data when the environmental outlier test is enabled.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void loadEnvironmentalData() throws ValidationManager.ValidationManagerException  {

        String detectEnvironmentalOutliers = this.properties.getProperty("Validation.detectEnvironmentalOutliers");

        // Load only if required
        if ( detectEnvironmentalOutliers != null && Boolean.parseBoolean( detectEnvironmentalOutliers ) ) {

            String environmentalVariables = this.properties.getProperty("Validation.environmentalVariables");

            // Check additional mandatory parameter
            if ( environmentalVariables == null || environmentalVariables.length() == 0 ) {

                String msg = "Property 'Validation.environmentalVariables' must be specified " +
                             "in the main configuration file when the environmental outlier " +
                             "detection test is active.";
                throw new ValidationManager.ValidationManagerException( msg );
            }

            // Instantiate lookup service
            SpatialAttributeLookup sal = new SpatialAttributeLookup();

            // Get available attributes from the service
            LinkedList<String> availableAttributes = sal.getAttributeNames();

            // Check if all parameters are available from the service
            StringTokenizer stringTokenizer = new StringTokenizer( environmentalVariables, "," );

            while ( stringTokenizer.hasMoreTokens() ) {

                String requestedVariable = stringTokenizer.nextToken();

                if ( ! availableAttributes.contains( requestedVariable ) ) {

                    String msg = "Variable '" + requestedVariable + "' specified as part of the " +
                                 "property 'Validation.environmentalVariables' in the main configuration " +
                                 "file is not available from the spatial attribute lookup service. " +
                                 "Please check the configuration.";
                    throw new ValidationManager.ValidationManagerException( msg );
                }

                // Add the concept using its name as the identifier, and double as the data type
                this.envVars.add( requestedVariable );
                this.envConcepts.add( new Concept( requestedVariable, DataType.DOUBLE ) );
            }

            // Validation target = OriginalRecords
            if ( this.target().equals( "OriginalRecords" ) ) {

                // For each original record
                List<Rec> bgRecords = this.bgRecSet.recs;
                for ( Rec record : bgRecords ) {

                    // For each environmental variable to be used
                    for ( int i = 0; i < this.envVars.size(); ++i ) {

                        String latStr = record.get( "declat" );
                        String longStr = record.get( "declong" );

                        String attribute = this.envVars.get(i);
                        Double value = null;

                        if ( longStr != null && longStr.length() > 0 && latStr != null && latStr.length() > 0 ) {

                            String salValue = sal.lookup( longStr, latStr, attribute );
                            if ( salValue != null && salValue.length() > 0 ) {

                                value = new Double( salValue );
                            }
                        }

                        record.envData.put( attribute, value );
                    }
                }
            }
            else {

                // Validation target = Interpretations

                // For each original record
                List<Rec> bgRecords = this.bgRecSet.recs;
                for ( Rec record : bgRecords ) {

                    // For each generated interpretation
                    List<Georef> georefs = record.georefs;
                    for ( Georef georef : georefs ) {

                        // For each environmental variable to be used
                        for ( int i = 0; i < this.envVars.size(); ++i ) {

                            Double latitude = new Double( georef.pointRadius.getY() );
                            Double longitude = new Double( georef.pointRadius.getX() );

                            String attribute = this.envVars.get(i);
                            Double value = null;

                            String salValue = sal.lookup( longitude.toString(), latitude.toString(), attribute );
                            if ( salValue != null && salValue.length() > 0 ) {

                                value = new Double( salValue );
                            }
                            georef.envData.put( attribute, value );
                        }
                    }
                }
            }
        }
    }

/**
 * <p>Main method to run validation.</p>
 *
 * @throws ValidationManager.ValidationManagerException
 */
    public void validate() throws ValidationManager.ValidationManagerException {

        System.out.println( "POST-PROCESSING\t\tStarting Validation using DataTester version " +
                            this.getDataTesterVersion() );

        this.loadRecordSet();

        String detectGeographicErrors = this.properties.getProperty("Validation.detectGeographicErrors");
        if ( detectGeographicErrors != null && 
             Boolean.parseBoolean( detectGeographicErrors ) ) {

            this.detectGeographicErrors();
        }

        String detectElevationErrors = this.properties.getProperty("Validation.detectElevationErrors");
        if ( detectElevationErrors != null && 
             Boolean.parseBoolean( detectElevationErrors ) ) {

            this.detectElevationErrors();
        }

        String detectEcologicalOutliers = this.properties.getProperty("Validation.detectEcologicalOutliers");
        if ( detectEcologicalOutliers != null && 
             Boolean.parseBoolean( detectEcologicalOutliers ) ) {

            this.detectEcologicalOutliers();
        }

        // For the last tests, activate record filter for interpretations
        if ( this.target().equals( "Interpretations" ) ) {

            this.genericRecordSet.setRecordFilter( this );
        }

        String detectGeographicOutliers = this.properties.getProperty("Validation.detectGeographicOutliers");
        if ( detectGeographicOutliers != null && 
             Boolean.parseBoolean( detectGeographicOutliers ) ) {

            this.detectGeographicOutliers();
        }

        String detectEnvironmentalOutliers = this.properties.getProperty("Validation.detectEnvironmentalOutliers");
        if ( detectEnvironmentalOutliers != null && 
             Boolean.parseBoolean( detectEnvironmentalOutliers ) ) {

            this.detectEnvironmentalOutliers();
        }

        String detectItineraryOutliers = this.properties.getProperty("Validation.detectItineraryOutliers");
        if ( detectItineraryOutliers != null && 
             Boolean.parseBoolean( detectItineraryOutliers ) ) {

            this.detectItineraryOutliers();
        }

        System.out.println( "POST-PROCESSING\t\tFinished Validation" );
    }

/**
 * <p>Geographic error detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectGeographicErrors() throws ValidationManager.ValidationManagerException  {

        System.out.println( "POST-PROCESSING\t\tStarting Geographic Error Detection" );

        String testInstanceId = "1";

        try {

            Map parameters = new HashMap();
            parameters.put( "longitudeConcept", BGConcepts.LONGITUDE.getId() );
            parameters.put( "latitudeConcept",  BGConcepts.LATITUDE.getId() );
            parameters.put( "administrativeRegionsConcepts", BGConcepts.COUNTRY.getId() + "," +
                                                             BGConcepts.STATE_PROVINCE.getId() + "," +
                                                             BGConcepts.COUNTY.getId() );
            parameters.put( "administrativeRegionsFeatures", "Country,StateProvince,County" );
            parameters.put( "featureDataReader", "org.biogeomancer.validation.DivaLookup" );

            DataTest geographicErrorTest = new GeographicErrorTest();
            geographicErrorTest.initialize( testInstanceId, this.knownConcepts, parameters );

            geographicErrorTest.test( this.genericRecordSet );
            this.geographicErrorResult = new GeographicErrorResult( this, geographicErrorTest.getResult() );
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Geographic Error Detection" );
    }

/**
 * <p>Elevation error detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectElevationErrors() throws ValidationManager.ValidationManagerException  {

        System.out.println( "POST-PROCESSING\t\tStarting Elevation Error Detection" );

        String testInstanceId = "2";

        try {

            Map parameters = new HashMap();
            parameters.put( "longitudeConcept", BGConcepts.LONGITUDE.getId() );
            parameters.put( "latitudeConcept",  BGConcepts.LATITUDE.getId() );
            parameters.put( "minimumElevationConcept", BGConcepts.MINIMUM_ELEVATION.getId() );
            parameters.put( "maximumElevationConcept", BGConcepts.MAXIMUM_ELEVATION.getId() );
            parameters.put( "uncertaintyConcept", BGConcepts.COORDINATE_UNCERTAINTY.getId() );
            parameters.put( "rasterDataReader", "org.biogeomancer.validation.DivaLookup" );
            parameters.put( "rasterAttributeId", "Elevation" );

            DataTest elevationErrorTest = new ElevationErrorTest();
            elevationErrorTest.initialize( testInstanceId, this.knownConcepts, parameters );

            elevationErrorTest.test( this.genericRecordSet );
            this.elevationErrorResult = new ElevationErrorResult( this, elevationErrorTest.getResult() );
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Elevation Error Detection" );
    }

/**
 * <p>Itinerary outlier detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectItineraryOutliers() throws ValidationManager.ValidationManagerException {

        System.out.println( "POST-PROCESSING\t\tStarting Itinerary Outlier Detection" );

        String testInstanceId = "3";

        try {

            Map parameters = new HashMap();
            parameters.put( "personConcept", BGConcepts.COLLECTOR.getId() );
            parameters.put( "latitudeConcept", BGConcepts.LATITUDE.getId() );
            parameters.put( "longitudeConcept", BGConcepts.LONGITUDE.getId() );
            parameters.put( "dateSource", BGConcepts.EARLIEST_COLLECTING_DATE.getId() );
            parameters.put( "dateFormat", "yyyy-MM-dd" );

            DataTest itineraryTest = new ItineraryTest();
            itineraryTest.initialize( testInstanceId, this.knownConcepts, parameters );

            if ( this.genericRecordSet.size() >= itineraryTest.minNumberOfRecords() ) {

                itineraryTest.test( this.genericRecordSet );
                this.itineraryResult = new ItineraryResult( this, itineraryTest.getResult() );
            }
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Itinerary Outlier Detection" );
    }

/**
 * <p>Geographic outlier detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectGeographicOutliers() throws ValidationManager.ValidationManagerException {

        System.out.println( "POST-PROCESSING\t\tStarting Geographic Outlier Detection" );

        String testInstanceId = "4";

        try {

            this.detectNumericOutliers( testInstanceId, BGConcepts.LONGITUDE );
            this.detectNumericOutliers( testInstanceId, BGConcepts.LATITUDE );

            this.latOutliers = this.getNumericOutlierResult( BGConcepts.LATITUDE );
            this.longOutliers = this.getNumericOutlierResult( BGConcepts.LONGITUDE );
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Geographic Outlier Detection" );
    }

/**
 * <p>Ecological outlier detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectEcologicalOutliers() throws ValidationManager.ValidationManagerException {

        System.out.println( "POST-PROCESSING\t\tStarting Ecological Outlier Detection" );

        String testInstanceId = "6";

        try {

            StringBuffer concepts = new StringBuffer( BGConcepts.LONGITUDE.getId() );
            concepts.append(",").append( BGConcepts.LATITUDE.getId() );
            concepts.append(",").append( BGConcepts.SCIENTIFIC_NAME.getId() );

            Map parameters = new HashMap();
            parameters.put( "externalClass", "org.biogeomancer.validation.DivaLookup" );
            parameters.put( "concepts", concepts.toString() );
            parameters.put( "minimumAcceptableProbability", "0.5" );

            DataTest probabilityProxyTest = new ProbabilityProxyTest();
            probabilityProxyTest.initialize( testInstanceId, this.knownConcepts, parameters );
            probabilityProxyTest.test( this.genericRecordSet );

            this.ecologicalOutlierResult = new EcologicalOutlierResult( this, probabilityProxyTest.getResult() );
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Ecological Outlier Detection" );
    }

/**
 * <p>Generic outlier detection.</p>
 *
 *
 * @param testInstanceId Test instance identifier
 * @param concept Concept to be tested
 * @throws DataTesterException
 */
    private void detectNumericOutliers( String testInstanceId, Concept concept ) 
        throws DataTesterException {

        // All numeric outliers will run per species
        this.partitionRecordSet( BGConcepts.SCIENTIFIC_NAME );

        Map parameters = new HashMap();
        parameters.put( "ignoreZero", "yes");
        parameters.put( "concept", concept.getId() );
        parameters.put( "tagAllRecordsForOutlierness", "yes" );

        // Use a single result manager for all partitions
        ResultManager resultManager = new ResultManager();

        // Dummy instance of the test just to get the minimum number of records
        DataTest tempNumericOutlierTest = new NumericOutlierTest();
        int minNumberOfRecords = tempNumericOutlierTest.minNumberOfRecords();

        // Objects to capture the scientific name
        GenericRecord firstRecord;
        String scientificName = "";

        // Instantiate a global outlier result object
        NumericOutlierResult numericOutlierResult = new NumericOutlierResult( this );

        // Run numeric outlier for each partition
        for ( GenericRecordSet partition : this.recordSetPartitions ) {

            // Get scientific name for the partition
            partition.resetCursor();
            firstRecord = partition.nextRecord();
            scientificName = firstRecord.getStringValue( BGConcepts.SCIENTIFIC_NAME );

            // Only run the test for partitions with the minimum size 
            if ( partition.size() >= minNumberOfRecords ) {

                DataTest numericOutlierTest = new NumericOutlierTest();
                numericOutlierTest.initialize( testInstanceId, this.knownConcepts, parameters );
                numericOutlierTest.test( partition );

                numericOutlierResult.addResult( scientificName, numericOutlierTest.getResult() );
            }
        }

        this.numericOutlierResults.put( concept, numericOutlierResult );
    }

/**
 * <p>Environmental outlier detection.</p>
 *
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private void detectEnvironmentalOutliers() throws ValidationManager.ValidationManagerException {

        System.out.println( "POST-PROCESSING\t\tStarted Environmental Outlier Detection" );

        String testInstanceId = "5";

        // Get environmental outlier threshold
        String environmentalOutlierThreshold = this.properties.getProperty("Validation.environmentalOutlierThreshold");
        int threshold = 1; // default value 
        if ( environmentalOutlierThreshold != null ) {

            threshold = Integer.parseInt( environmentalOutlierThreshold );

            if ( threshold > this.envConcepts.size() ) {

                String msg = "Property 'Validation.environmentalOutlierThreshold' must not " +
                             "exceed the number of environmental variables specified!";
                throw new ValidationManager.ValidationManagerException( msg );
            }
        }

        // Run test for each variable
        try {

            for ( Concept envConcept : this.envConcepts ) {

                this.detectNumericOutliers( testInstanceId, envConcept );

                this.environmentalOutlierResult = new EnvironmentalOutlierResult( this, this.envConcepts, threshold );
            }
        }
        catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }

        System.out.println( "POST-PROCESSING\t\tFinished Environmental Outlier Detection" );
     }

/**
 * <p>Populates the recordSetPartitions property with Generic Record Sets containing
 * records for each distinct value of the concept that was passed as a parameter.</p>
 *
 *
 * @param concept Concept to be tested
 * @throws DataTesterException
 */
    private void partitionRecordSet( Concept concept ) 
        throws DataTesterException {

        if ( this.recordSetPartitions != null ) {

            return;
        }

        LinkedHashMap criteria = new LinkedHashMap();
        Boolean ascendingOrder = new Boolean( true );
        criteria.put( concept, ascendingOrder );

        this.recordSetPartitions = RecordSetPartitioner.getPartitions( this.genericRecordSet, criteria );
    }

/**
 * <p>Returns an instance of NumericOutlierResult related with the specified Concept.</p>
 *
 *
 * @param concept Related Concept.
 * @return Instance of NumericOutlierResult.
 *
 * @throws DataTesterException
 */
    public NumericOutlierResult getNumericOutlierResult( Concept concept ) {

        return (NumericOutlierResult)this.numericOutlierResults.get( concept );
    }

/**
 * <p>Returns an instance of ItineraryResult.</p>
 *
 *
 * @return Instance of ItineraryResult.
 *
 * @throws DataTesterException
 */
    public ItineraryResult getItineraryResult() {

        return this.itineraryResult;
    }

/**
 * <p>Returns the corresponding DataTester Target for a single interpretation record.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @return Corresponding DataTester Target.
 */
    public Target getDataTesterTarget( int recIndex, int georefIndex ) {

        Map dataMap = new InterpretationDataMap( (InterpretationRecordSet) this.genericRecordSet, 
                                                 recIndex, georefIndex );

        GenericRecord genericRecord = new RamRecord( InterpretationRecordSet.ID_DEFINITION, 
                                                     dataMap );

        return new Target ( genericRecord );
    }

/**
 * <p>Returns the corresponding DataTester Target for a single original record.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @return Corresponding DataTester Target.
 */
    public Target getDataTesterTarget( int recIndex ) {

        Map dataMap = new OriginalRecordDataMap( (OriginalRecordSet) this.genericRecordSet,
                                                 recIndex );

        GenericRecord genericRecord = new RamRecord( OriginalRecordSet.ID_DEFINITION, 
                                                     dataMap );

        return new Target ( genericRecord );
    }

/**
 * <p>Returns the first AssignedTag occurrence of the specified type, or null if
 * there has been no assignment of the specified type.</p>
 *
 *
 * @param resultManager Related ResultManager instance.
 * @param tagId Tag id.
 * @return First AssignedTag of the specified type or null.
 */
    public AssignedTag getFirstAssignedTag( ResultManager resultManager, String tagId ) {

        Collection relatedTags = resultManager.getAssignedTags( tagId );

        if ( relatedTags.size() > 0 ) {

            // Get first occurrence
            Iterator i = relatedTags.iterator();
            return (AssignedTag) i.next();
        }

        return null;
    }

/**
 * <p>Returns the first AssignedTag occurrence of the specified type related to the
 * specified target, or null if there has been no assignment matching these requirements.</p>
 *
 *
 * @param resultManager Related ResultManager instance.
 * @param target Target.
 * @param tagId Tag id.
 * @return First AssignedTag of the specified type or null.
 */
    public AssignedTag getFirstAssignedTag( ResultManager resultManager, Target target, String tagId ) {

        Collection relatedTags = resultManager.getAssignedTags( target, tagId );

        if ( relatedTags.size() > 0 ) {

            // Get first occurrence
            Iterator i = relatedTags.iterator();
            return (AssignedTag) i.next();
        }

        return null;
    }

/**
 * <p>Indicates if a {@link GenericRecord} should be skipped for some reason. This 
 * method is implemented according to the RecordFilter interface from the 
 * DataTester framework and is not supposed to be called from any other
 * context except the GenericRecordSet implementation being used.</p>
 * 
 * @param genericRecord {@link GenericRecord} to be verified.
 * @return True if the specified record should be skipped.
 */
    public boolean skipRecord( GenericRecord genericRecord ) {

        try {

            Integer recIndex = genericRecord.getIntegerValue( BGConcepts.RECORD_INDEX );
            Integer georefIndex = genericRecord.getIntegerValue( BGConcepts.GEOREF_INDEX );

            Rec rec = this.bgRecSet.recs.get( recIndex );

            // Do not skip interpretation if it's the only one associated with the record
            if ( rec.georefs.size() == 1 ) {

                return false;
            }

            // From here we only deal with multiple interpretations associated with the same record

            // Skip interpretation if the corresponding record is already marked to be skipped
            if ( this.skippedRecords.contains( recIndex ) ) {

                return true;
            }

            // Use cache if a remaining interpretation was already determined
            if ( this.remainingInterpretations.containsKey( recIndex ) ) {

                if ( this.remainingInterpretations.get( recIndex ).equals( georefIndex) ) {

                    return false;
                }
                else {

                    // Interpretation was previously considered suspect
                    return true;
                }
            }
            else {

                // Check if there's a remaining interpretation that can be used
                int nonSuspectInterpretations = 0;
                int lastNonSuspectGeorefIndex = -1;

                for ( int i = 0; i <= rec.georefs.size(); ++i ) {

                    if ( ! this.isSuspectAccordingToIndividualTests( recIndex, i) ) {

                        lastNonSuspectGeorefIndex = i;
                        ++nonSuspectInterpretations;
                    }
                }

                if ( nonSuspectInterpretations == 1 ) {

                    // If there is one and only one "valid" interpretation, use it and
                    // store it in the cache
                    this.remainingInterpretations.put( recIndex, lastNonSuspectGeorefIndex );
                    return false;
                }
                else {

                    // Record should be skipped when there are no "valid" interpretations
                    // or more than one valid interpretations
                    this.skippedRecords.add( recIndex );
                    return true;
                }
            }

        } catch ( Exception exception ) {

            // Nothing to do here if there's an exception
            throw new RuntimeException( exception );
        }
    }

/**
 * <p>Indicates if some problem was detected with the specified interpretation during
 * the individual tests (tests that can run on each record separately, as opposed
 * to tests that depend on the record set context).</p>
 * 
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 * @return True if the interpretation is suspect, false otherwise.
 *
 * @throws ValidationManager.ValidationManagerException
 */
    private boolean isSuspectAccordingToIndividualTests( int recIndex, int georefIndex ) throws ValidationManager.ValidationManagerException {

        if ( !this.enabled() || !this.target().equals("Interpretations") ) {

            return true;
        }

        if ( this.geographicErrorResult != null ) {

            GeographicError geographicError = this.geographicErrorResult.getError( recIndex, georefIndex );

            if ( geographicError != null ) {

                return true;
            }
        }

        if ( this.elevationErrorResult != null ) {

            ElevationError elevationError = this.elevationErrorResult.getError( recIndex, georefIndex );

            if ( elevationError != null ) {

                return true;
            }
        }

        if ( this.ecologicalOutlierResult != null && 
             this.ecologicalOutlierResult.isOutlier( recIndex, georefIndex ) ) {

            return true;
        }

        return false;
    }

/**
 * <p>Prints validation results associated with a specific original record.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 *
 * @throws ValidationManager.ValidationManagerException
 */
    public void displayResults( int recIndex ) throws ValidationManager.ValidationManagerException {

        try {

            if ( !this.enabled() || !this.target().equals("OriginalRecords") ) {

                return;
            }

            System.out.println("\tValidation: ");

            if ( this.geographicErrorResult != null ) {

                GeographicError geographicError = this.geographicErrorResult.getError( recIndex );

                if ( geographicError != null ) {

                    System.out.println("\t\tGeographic error:");

                    if ( geographicError.hasErrorInCountry() ) {

                        System.out.println("\t\t\tCountry feature--> " + geographicError.getCountryFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.COUNTRY ));
                    }
                    if ( geographicError.hasErrorInStateProvince() ) {

                        System.out.println("\t\t\tState/province feature--> " + geographicError.getStateProvinceFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.STATE_PROVINCE ));
                    }
                    if ( geographicError.hasErrorInCounty() ) {

                        System.out.println("\t\t\tCounty feature--> " + geographicError.getCountyFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.COUNTY ));
                    }
                }
            }

            if ( this.elevationErrorResult != null ) {

                ElevationError elevationError = this.elevationErrorResult.getError( recIndex );

                if ( elevationError != null ) {

                    System.out.println("\t\tElevation error:");

                    System.out.println("\t\t\tCalculated elevation--> " + elevationError.getCalculatedElevation() + 
                                       " (min = " + elevationError.getCalculatedMinimumElevation() + 
                                       " / max = " + elevationError.getCalculatedMaximumElevation() + ")");
                }
            }

            if ( this.ecologicalOutlierResult != null ) {

                if ( this.ecologicalOutlierResult.isOutlier( recIndex ) ) {

                    System.out.println("\t\tEcological outlier: yes (probability of presence = " + this.ecologicalOutlierResult.getProbability( recIndex )*100 + "%)");
                }
                else {

                    System.out.println("\t\tEcological outlier: no");
                }
            }

            if ( this.itineraryResult != null ) {

                Collection itineraryOutliers = this.itineraryResult.getOutliers( recIndex );

                if ( itineraryOutliers.isEmpty() ) {

                    System.out.println("\t\tItinerary outlier: no");
                }
                else {

                    System.out.println("\t\tItinerary outlier: yes");

                    for ( Iterator i = itineraryOutliers.iterator(); i.hasNext(); ) {

                        ItineraryOutlier outlier = (ItineraryOutlier) i.next();

                        double calculatedDistanceInKilometers = outlier.getCalculatedDistance();
                        double maximumDistanceInKilometers = outlier.getMaximumDistance();
                        int numberOfDays = outlier.getNumberOfDays();
                        String date = outlier.getDate();
                        String otherRecordId = outlier.getOtherRecordId();

                        System.out.println("\t\t\tVery strange that someone travelled " + calculatedDistanceInKilometers + " kilometers in " + numberOfDays + " day(s) by the time of " + date + ". The maximum distance should not exceed " + maximumDistanceInKilometers + " kilometers per day. Comparison performed with record identified by " + otherRecordId + ".");
                    }
                }
            }

            if ( this.latOutliers != null && this.latOutliers.skippedRecord( recIndex ) ) {

                System.out.println("\t\tSkipped geographical and environmental outlier tests (not enough records for the species).");
            }
            else {

                if ( this.latOutliers != null && this.longOutliers != null ) {

                    System.out.println("\t\tLatitude:");
                    System.out.println("\t\t\tOutlierness--> " + this.latOutliers.getOutlierness( recIndex ));
                    System.out.println("\t\t\tOutlier--> " + this.latOutliers.isOutlier( recIndex ));
                    System.out.println("\t\tLongitude:");
                    System.out.println("\t\t\tOutlierness--> " + this.longOutliers.getOutlierness( recIndex ));
                    System.out.println("\t\t\tOutlier--> " + this.longOutliers.isOutlier( recIndex ));
                }

                if ( this.environmentalOutlierResult != null ) {

                    EnvironmentalOutlier environmentalOutlier = this.environmentalOutlierResult.getOutlier( recIndex );

                    if ( environmentalOutlier == null ) {

                        System.out.println("\t\tEnvironmental outlier: no");
                    }
                    else {

                        System.out.println("\t\tEnvironmental outlier: yes");

                        System.out.println("\t\t\tFailed in " + environmentalOutlier.getOutlierVariables().size() + " layers");
                        System.out.println("\t\t\tAverage outlierness--> " + environmentalOutlier.getAverageOutlierness());
                    }
                }
            }

        } catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }
    }

/**
 * <p>Prints validation results associated with a specific interpretation.</p>
 *
 *
 * @param recIndex Index of the original record in the record set.
 * @param georefIndex Index of the interpretation in the original record.
 *
 * @throws ValidationManager.ValidationManagerException
 */
    public void displayResults( int recIndex, int georefIndex ) throws ValidationManager.ValidationManagerException {

        try {

            if ( !this.enabled() || !this.target().equals("Interpretations") ) {

                return;
            }

            System.out.println("\tValidation: ");

            if ( this.geographicErrorResult != null ) {

                GeographicError geographicError = this.geographicErrorResult.getError( recIndex, georefIndex );

                if ( geographicError != null ) {

                    System.out.println("\t\tGeographic error:");

                    if ( geographicError.hasErrorInCountry() ) {

                        System.out.println("\t\t\tCountry feature--> " + geographicError.getCountryFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.COUNTRY ));
                    }
                    if ( geographicError.hasErrorInStateProvince() ) {

                        System.out.println("\t\t\tState/province feature--> " + geographicError.getStateProvinceFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.STATE_PROVINCE ));
                    }
                    if ( geographicError.hasErrorInCounty() ) {

                        System.out.println("\t\t\tCounty feature--> " + geographicError.getCountyFeature());
                        System.out.println("\t\t\t\tdistance measured = " + geographicError.getDistance( BGConcepts.COUNTY ));
                    }
                }
            }

            if ( this.elevationErrorResult != null ) {

                ElevationError elevationError = this.elevationErrorResult.getError( recIndex, georefIndex );

                if ( elevationError != null ) {

                    System.out.println("\t\tElevation error:");

                    System.out.println("\t\t\tCalculated elevation--> " + elevationError.getCalculatedElevation() + 
                                       " (min = " + elevationError.getCalculatedMinimumElevation() + 
                                       " / max = " + elevationError.getCalculatedMaximumElevation() + ")");
                }
            }

            if ( this.ecologicalOutlierResult != null ) {

                if ( this.ecologicalOutlierResult.isOutlier( recIndex, georefIndex ) ) {

                    System.out.println("\t\tEcological outlier: yes (probability of presence = " + this.ecologicalOutlierResult.getProbability( recIndex, georefIndex )*100 + "%)");
                }
                else {

                    System.out.println("\t\tEcological outlier: no");
                }
            }

            if ( this.itineraryResult != null ) {

                Collection itineraryOutliers = this.itineraryResult.getOutliers( recIndex, georefIndex );

                if ( itineraryOutliers.isEmpty() ) {

                    System.out.println("\t\tItinerary outlier: no");
                }
                else {

                    System.out.println("\t\tItinerary outlier: yes");

                    for ( Iterator i = itineraryOutliers.iterator(); i.hasNext(); ) {

                        ItineraryOutlier outlier = (ItineraryOutlier) i.next();

                        double calculatedDistanceInKilometers = outlier.getCalculatedDistance();
                        double maximumDistanceInKilometers = outlier.getMaximumDistance();
                        int numberOfDays = outlier.getNumberOfDays();
                        String date = outlier.getDate();
                        String otherRecordId = outlier.getOtherRecordId();

                        System.out.println("\t\t\tVery strange that someone travelled " + calculatedDistanceInKilometers + " kilometers in " + numberOfDays + " day(s) by the time of " + date + ". The maximum distance should not exceed " + maximumDistanceInKilometers + " kilometers per day. Comparison performed with record identified by " + otherRecordId + ".");
                    }
                }
            }

            if ( this.latOutliers != null && this.latOutliers.skippedRecord( recIndex, georefIndex ) ) {

                System.out.println("\t\tSkipped geographical and environmental outlier tests (not enough records for the species).");
            }
            else {

                if ( this.latOutliers != null && this.longOutliers != null ) {

                    System.out.println("\t\tLatitude:");
                    System.out.println("\t\t\tOutlierness--> " + this.latOutliers.getOutlierness( recIndex, georefIndex ));
                    System.out.println("\t\t\tOutlier--> " + this.latOutliers.isOutlier( recIndex, georefIndex ));
                    System.out.println("\t\tLongitude:");
                    System.out.println("\t\t\tOutlierness--> " + this.longOutliers.getOutlierness( recIndex, georefIndex ));
                    System.out.println("\t\t\tOutlier--> " + this.longOutliers.isOutlier( recIndex, georefIndex ));
                }

                if ( this.environmentalOutlierResult != null ) {

                    EnvironmentalOutlier environmentalOutlier = this.environmentalOutlierResult.getOutlier( recIndex, georefIndex );

                    if ( environmentalOutlier == null ) {

                        System.out.println("\t\tEnvironmental outlier: no");
                    }
                    else {

                        System.out.println("\t\tEnvironmental outlier: yes");

                        System.out.println("\t\t\tFailed in " + environmentalOutlier.getOutlierVariables().size() + " layers");
                        System.out.println("\t\t\tAverage outlierness--> " + environmentalOutlier.getAverageOutlierness());
                    }
                }
            }

        } catch ( DataTesterException exception ) {

            throw new ValidationManager.ValidationManagerException( exception.getMessage() );
        }
    }

/**
 * <p>Prints a summary of validation results.</p>
 *
 *
 */
    public void displaySummary() {

        if ( !this.enabled() ) {

            return;
        }

// NOTE: The following information doesn't make sense anymore to be displayed here, since outliers
// are now calculated per species. The methods getMinimumValue and getMaximumValue are still available,
// but require a scientific name as parameter. 
//
//         System.out.println("Other validation details: ");
//
//         if ( this.latOutliers != null && this.longOutliers != null ) {
//
//             System.out.println("\tAdditional info about geographical outliers:");
//             System.out.println("\t\tLatitude:");
//             System.out.println("\t\t\tMinimum value of the acceptable range--> " + this.latOutliers.getMinimumValue());
//             System.out.println("\t\t\tMaximum value of the acceptable range--> " + this.latOutliers.getMaximumValue());
//             System.out.println("\t\tLongitude:");
//             System.out.println("\t\t\tMinimum value of the acceptable range--> " + this.longOutliers.getMinimumValue());
//             System.out.println("\t\t\tMaximum value of the acceptable range--> " + this.longOutliers.getMaximumValue());
//         }
//
//         if ( this.environmentalOutlierResult != null ) {
//
//             System.out.println("\tAdditional info about environmental outliers:");
//
//             for ( Concept concept : this.envConcepts ) {
//
//                 NumericOutlierResult numericOutlierResult = this.getNumericOutlierResult( concept );
//
//                 System.out.println("\t\t" + concept.getId() + ":");
//                 System.out.println("\t\t\tMinimum value of the acceptable range--> " + numericOutlierResult.getMinimumValue());
//                 System.out.println("\t\t\tMaximum value of the acceptable range--> " + numericOutlierResult.getMaximumValue());
//             }
//         }
    }
 }
