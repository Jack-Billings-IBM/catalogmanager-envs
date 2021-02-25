      ******************************************************************
      *                                                                *
      * CONTROL BLOCK NAME = DFH0XCP3                                  *
      *                                                                *
      * DESCRIPTIVE NAME = CICS TS  (Samples) Example Application -    *
      *                     Main copybook for example application      *
      *                                                                *
      *                                                                *
      *                                                                *
      *      Licensed Materials - Property of IBM                      *
      *                                                                *
      *      "Restricted Materials of IBM"                             *
      *                                                                *
      *      5655-Y04                                                  *
      *                                                                *
      *      (C) Copyright IBM Corp. 2004"                             *
      *                                                                *
      *                                                                *
      *                                                                *
      *                                                                *
      * STATUS = 7.1.0                                                 *
      *                                                                *
      * FUNCTION =                                                     *
      *      This copy book is part of the example application and     *
      *      defines the datastructure for an inquire list for the     *
      *      catalogitems. It is the same as the structure defined     *
      *      DFH0XCP1 but without the redefines                        *
      *----------------------------------------------------------------*
      *                                                                *
      * CHANGE ACTIVITY :                                              *
      *      $SEG(DFH0XCP3),COMP(SAMPLES),PROD(CICS TS ):              *
      *                                                                *
      *   PN= REASON REL YYMMDD HDXXIII : REMARKS                      *
      *   $D0= I07544 640 040910 HDIPCB  : EXAMPLE - BASE APPLICATION  *
      *                                                                *
      ******************************************************************
      *    Catalogue COMMAREA structure
           03 CA-REQUEST-ID            PIC X(6).
           03 CA-RETURN-CODE           PIC 9(2) DISPLAY.
           03 CA-RESPONSE-MESSAGE      PIC X(79).
      *    Fields used in Inquire Catalog
           03 CA-INQUIRE-REQUEST.
               05 CA-LIST-START-REF        PIC 9(4) DISPLAY.
               05 CA-LAST-ITEM-REF         PIC 9(4) DISPLAY.
               05 CA-ITEM-COUNT            PIC 9(3) DISPLAY.
               05 CA-CAT-ITEM OCCURS 15.
                   07 CA-ITEM-REF          PIC 9(4) DISPLAY.
                   07 CA-DESCRIPTION       PIC X(40).
                   07 CA-DEPARTMENT        PIC 9(3) DISPLAY.
                   07 CA-COST              PIC X(6).
                   07 IN-STOCK             PIC 9(4) DISPLAY.
                   07 ON-ORDER             PIC 9(3) DISPLAY.

