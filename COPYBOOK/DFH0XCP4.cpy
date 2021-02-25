      ******************************************************************
      *                                                                *
      * CONTROL BLOCK NAME = DFH0XCP4                                  *
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
      *      defines the datastructure for an inquire single for a     *
      *      catalog item. It is the same as the structure defined     *
      *      DFH0XCP1 but without the redefines                        *
      *----------------------------------------------------------------*
      *                                                                *
      * CHANGE ACTIVITY :                                              *
      *      $SEG(DFH0XCP4),COMP(SAMPLES),PROD(CICS TS ):              *
      *                                                                *
      *   PN= REASON REL YYMMDD HDXXIII : REMARKS                      *
      *   $D0= I07544 640 040910 HDIPCB  : EXAMPLE - BASE APPLICATION  *
      *                                                                *
      ******************************************************************
      *    Catalogue COMMAREA structure
           03 CA-SINGLE-REQUEST-ID            PIC X(6).
           03 CA-SINGLE-RETURN-CODE           PIC 9(2) DISPLAY.
           03 CA-SINGLE-RESPONSE-MESSAGE      PIC X(79).
      *    Fields used in Inquire Single
           03 CA-INQUIRE-SINGLE.
               05 CA-ITEM-REF-REQ          PIC 9(4) DISPLAY.
               05 FILLER                   PIC 9(4) DISPLAY.
               05 FILLER                   PIC 9(3) DISPLAY.
               05 CA-SINGLE-ITEM.
                   07 CA-SNGL-ITEM-REF     PIC 9(4) DISPLAY.
                   07 CA-SNGL-DESCRIPTION  PIC X(40).
                   07 CA-SNGL-DEPARTMENT   PIC 9(3) DISPLAY.
                   07 CA-SNGL-COST         PIC X(6).
                   07 IN-SNGL-STOCK        PIC 9(4) DISPLAY.
                   07 ON-SNGL-ORDER        PIC 9(3) DISPLAY.





