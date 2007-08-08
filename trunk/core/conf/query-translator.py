# =============================================================================
#
# query-translator.py
#
# Query translator configuration file for the ADL Gazetteer,
# specifically, the 'gazsearch' database.
#
# This file assumes that gazetteer protocol queries have been
# converted to bucket constraints as documented below.  The paradigms
# and configuration options given in this file then govern the
# translation of bucket constraints to SQL.  A companion file,
# 'bucket99.conf', is required to supply the place status and feature
# type vocabularies.
#
# In the following, each protocol query is described in pseudo-XML;
# the equivalent bucket constraint is described in the form of a
# edu.ucsb.adl.middleware.Query.*Constraint constructor; and an
# example demonstrates the overall translation of a complete sample
# protocol query to an SQL query.
#
# 1. <identifier-query identifier=I>
#
#     Bucket equivalent:
#
#         IdentificationConstraint("gaz:identifier-query", null,
#         "matches", I, "ADL Gazetteer external identifier")
#
#     Translation example:
#
#         <identifier-query identifier="adlgaz-1-600-1c"/>
#
#         SELECT A.feature_id FROM i_feature A WHERE
#         A.feature_id = 600
#
# 2. <code-query [scheme=S] code=C>
#
#     Bucket equivalent:
#
#         IdentificationConstraint("gaz:code-query", null, "matches",
#         C, S)
#
#     Translation examples:
#
#         <code-query code="1102-13A"/>
#
#         SELECT A.feature_id FROM i_feature_code A WHERE
#         A.code = '1102-13A'
#
#         <code-query scheme="Catalog of Active Volcanoes of the World
#         (CAVW) Volcano Number" code="1102-13A"/>
#
#         SELECT B.feature_id FROM i_feature_code B, i_scheme A WHERE
#         A.scheme_id = B.code_scheme_id AND A.scheme_name =
#         'Catalog of Active Volcanoes of the World (CAVW) Volcano Number'
#         AND B.code = '1102-13A'
#
# 3. <place-status-query status=S>
#
#     Bucket equivalent:
#
#         HierarchicalConstraint("gaz:place-status-query", null,
#         "is-a", S, "ADL Gazetteer Protocol place status terms")
#
#     Translation example:
#
#         <place-status-query status="current"/>
#
#         SELECT A.feature_id FROM i_feature A WHERE
#         A.place_status_term_id IN (922)
#
# 4. <name-query operator=O text=T>
#
#     Bucket equivalent:
#
#         TextualConstraint("gaz:name-query", null, O, T)
#
#         Note: the current paradigm (Textual_SuffixTable) does not
#         support Unicode.
#
#     Translation example:
#
#         <name-query operator="contains-all-words"
#         text="goleta beach"/>
#
#         SELECT B.feature_id FROM i_feature_name_text B,
#         i_feature_name_text A WHERE A.feature_id = B.feature_id AND
#         A.phrase_id = B.phrase_id AND B.text LIKE 'GOLETA;%' AND
#         A.text LIKE 'BEACH;%'
#
# 5a. <footprint-query operator=O box=(N, S, E, W)>
#
#     Bucket equivalent:
#
#         SpatialConstraint("gaz:footprint-query", null, O, N, S,
#         E, W)
#
#         Note: in converting coordinates in the <gml:Box> element to
#         separate (N, S, E, W) coordinates, longitudes must be
#         normalized to the range [-180,180].  Also, the "within"
#         operator must be renamed to "is-contained-in".
#
#     Translation example:
#
#         <footprint-query operator="overlaps">
#             <gml:Box>
#                 <gml:coordinates>-140,30 -110,35</gml:coordinates>
#             </gml:Box>
#         </footprint-query>
#
#         SELECT A.feature_id FROM i_feature_footprint A WHERE
#         A.footprint && GeometryFromText('POLYGON((-140.0 30.0,
#         -140.0 35.0, -110.0 35.0, -110.0 30.0, -140.0 30.0))',
#         54001)
#
# 5b. <footprint-query operator=O polygon=P>
#
#     Bucket equivalent:
#
#         SpatialConstraint("gaz:footprint-query", null, O, P)
#
#         Note: the current paradigm (Spatial_PostgreSQLBoxNoCrossing)
#         does not support searching over polygonal query regions.
#         Also, there are unresolved issues in converting the
#         <gml:Polygon> element to a middleware-style spherical
#         polygon.
#
# 5c. <footprint-query operator=O identifier=I>
#
#     Bucket equivalent:
#
#         Not directly supported.  In a preprocessing step, I must be
#         resolved into a box or polygon, or the query must be
#         reformulated as a relationship query.
#
# 6. <class-query thesaurus=V term=T>
#
#     Bucket equivalent:
#
#         HierarchicalConstraint("gaz:class-query", null, "is-a",
#         T, V)
#
#         Note: the supported vocabularies are defined in companion
#         file 'bucket99.conf'.
#
#     Translation example:
#
#         <class-query thesaurus="ADL Feature Type Thesaurus"
#         term="land regions"/>
#
#         SELECT A.feature_id FROM i_classification A WHERE
#         A.classification_term_id IN (719, 751, 796, 800, 915)
#
# 7. <relationship-query relation=R target-identifier=I>
#
#     Bucket equivalent:
#
#         RelationalConstraint("gaz:relationship-query", null, R,
#         "gaz:" + I)
#
#         Note: the supported relationships are defined in this file
#         in the Local_Relationships paradigm below.
#
#     Translation example:
#
#         <relationship-query relation="part of"
#         target-identifier="adlgaz-1-600-1c"/>
#
#         SELECT B.feature_id FROM i_related_feature B,
#         i_scheme_term A WHERE
#         A.scheme_term_id = B.related_type_term_id AND
#         A.term = 'part of' AND
#         B.related_feature_feature_id = 600
#
# The format of ADL Gazetteer external identifiers, which is
# referenced by local paradigms below, is described in
# <http://www.alexandria.ucsb.edu/~gjanee/archive/2002/external-ids.html>.
#
# Greg Janee
# gjanee@alexandria.ucsb.edu
#
# February 2004
#
# -----------------------------------------------------------------------------

import re

import edu.ucsb.adl.middleware
M = edu.ucsb.adl.middleware

import UniversalTranslator
UT = UniversalTranslator

import paradigms
P = paradigms

# Local paradigms...

class Local_ADLGazetteerExternalIdentifiers (UT.Paradigm):
    def __init__ (self):
        self.paradigm = P.Identification_Integer(
            table="i_feature",
            idColumn="feature_id",
            matchColumn="feature_id",
            namespaces=["ADL Gazetteer internal identifier"],
            cardinality=UT.Cardinality("1"))
        self.pattern = re.compile("(adlgaz-1-(\d+))-([\da-fA-F]{2})")
    def translateBucketAtomic (self, constraint, vocabularies):
        if constraint.getNamespace() != None and\
            constraint.getNamespace() != "ADL Gazetteer external identifier":
            raise UT.QueryError, "unsupported namespace"
        match = self.pattern.match(constraint.getIdentifier())
        if match:
            id = match.group(1)
            internalId = match.group(2)
            checksum = int(match.group(3), 16)
            sum = 0
            for i in range(len(id)):
                sum += ord(id[i])*(i+1)
            if sum%131 == checksum:
                return self.paradigm.translateBucketAtomic(
                    M.Query.IdentificationConstraint(constraint.getBucket(),
                        constraint.getField(), constraint.getOperator(),
                        internalId, "ADL Gazetteer internal identifier"),
                    vocabularies)
            else:
                raise UT.QueryError, "corrupt identifier"
        else:
            raise UT.QueryError, "invalid identifier"

class Local_FeatureCodes (UT.Paradigm):
    def __init__ (self):
        self.paradigm = P.Adaptor_IndirectQualification(
            mainFieldColumn="code_scheme_id",
            auxiliaryTable="i_scheme",
            auxiliaryFieldColumn="scheme_id",
            auxiliaryUriColumn="scheme_name",
            paradigm=P.Identification_String(
                table="i_feature_code",
                idColumn="feature_id",
                matchColumn="code",
                namespaces=[],
                cardinality=UT.Cardinality("0+")))
    def translateBucketAtomic (self, constraint, vocabularies):
        if constraint.getNamespace() != None:
            f = M.Query.Field()
            f.uri = constraint.getNamespace()
            f.name = constraint.getNamespace()
            return self.paradigm.translateFieldAtomic(
                M.Query.IdentificationConstraint(constraint.getBucket(), f,
                    constraint.getOperator(), constraint.getIdentifier(),
                    None),
                vocabularies)
        else:
            return self.paradigm.translateBucketAtomic(constraint,
                vocabularies)

class Local_Relationships (UT.Paradigm):
    def __init__ (self):
        self.paradigm = P.Relational_Integer(
            table="i_related_feature",
            idColumn="feature_id",
            relationColumn="related_type_term_id",
            targetColumn="related_feature_feature_id",
            cardinality=UT.Cardinality("0+"),
            prefix="gaz:",
            relationTable="i_scheme_term",
            relationTableIdColumn="scheme_term_id",
            relationTableRelationColumn="term")
        self.pattern = re.compile("gaz:(adlgaz-1-(\d+))-([\da-fA-F]{2})")
    def translateBucketAtomic (self, constraint, vocabularies):
        match = self.pattern.match(constraint.getTargetIdentifier())
        if match:
            id = match.group(1)
            internalId = match.group(2)
            checksum = int(match.group(3), 16)
            sum = 0
            for i in range(len(id)):
                sum += ord(id[i])*(i+1)
            if sum%131 == checksum:
                return self.paradigm.translateBucketAtomic(
                    M.Query.RelationalConstraint(constraint.getBucket(),
                        constraint.getField(), constraint.getOperator(),
                        "gaz:" + str(internalId)),
                    vocabularies)
            else:
                raise UT.QueryError, "corrupt identifier"
        else:
            raise UT.QueryError, "invalid identifier"

# Bucket mappings...

buckets = {

"gaz:identifier-query" :
    UT.Bucket(
        "identification",
        UT.standardIdentificationOperators,
        Local_ADLGazetteerExternalIdentifiers()),

"gaz:code-query" :
    UT.Bucket(
        "identification",
        UT.standardIdentificationOperators,
        Local_FeatureCodes()),

"gaz:place-status-query" :
    UT.Bucket(
        "hierarchical",
        UT.standardHierarchicalOperators,
        P.Hierarchical_IntegerSet(
            table="i_feature",
            idColumn="feature_id",
            codeColumn="place_status_term_id",
            cardinality=UT.Cardinality("1"))),

"gaz:name-query" :
    UT.Bucket(
        "textual",
        UT.extendedTextualOperators,
        P.Textual_SuffixTable(
            table="i_feature_name_text",
            idColumn="feature_id",
            textColumn="text",
            delimiter=";",
            cardinality=UT.Cardinality("1+"),
            phraseNumColumn="phrase_id",
            mapping=\
                P.TextUtils.mappings.uppercaseAlphanumericOthersToWhitespace)),

"gaz:taxonomic-query" :
    UT.Bucket(
        "textual",
        UT.extendedTextualOperators,
        P.Textual_SuffixTable(
            table="i_feature_taxa_text",
            idColumn="feature_id",
            textColumn="text",
            delimiter=";",
            cardinality=UT.Cardinality("1+"),
            phraseNumColumn="phrase_id",
            mapping=\
                P.TextUtils.mappings.uppercaseAlphanumericOthersToWhitespace)),

"gaz:footprint-query" :
    UT.Bucket(
        "spatial",
        UT.standardSpatialOperators,
        P.Spatial_PostgreSQLBoxNoCrossing(
            table="i_feature_footprint",
            idColumn="feature_id",
            geometryColumn="footprint",
            cardinality=UT.Cardinality("1"),
            usePostGisSyntax=1,
            srid=54001)),

"gaz:class-query" :
    UT.Bucket(
        "hierarchical",
        UT.standardHierarchicalOperators,
        P.Hierarchical_IntegerSet(
            table="i_classification",
            idColumn="feature_id",
            codeColumn="classification_term_id",
            cardinality=UT.Cardinality("1+"))),

"gaz:relationship-query" :
    UT.Bucket(
        "relational",
        ["part of"],
        Local_Relationships())

}

translator = UT.Translator(buckets)

def translate ():
    return translator.translate(constraint, vocabularies)
