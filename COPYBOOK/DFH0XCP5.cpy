      ******************************************************************
      *                                                                *
      * CONTROL BLOCK NAME = DFH0XCP5                                  *
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
      * STATUS = 7.2.0                                                 *
      *                                                                *
      * FUNCTION =                                                     *
      *      This copy book is part of the example application and     *
      *      defines the datastructure for to place an order for a     *
      *      catalog item. It is the same as the structure defined     *
      *      DFH0XCP1 but without the redefines                        *
      *----------------------------------------------------------------*
      *                                                                *
      * CHANGE ACTIVITY :                                              *
      *      $SEG(DFH0XCP5),COMP(SAMPLES),PROD(CICS TS ):              *
      *                                                                *
      *   PN= REASON REL YYMMDD HDXXIII : REMARKS                      *
      *   $D0= I07544 640 040910 HDIPCB  : EXAMPLE - BASE APPLICATION  *
      *                                                                *
      ******************************************************************

      *    Catalogue COMMAREA structure
      *     03 CA-REQUEST-ID            PIC X(6).
      *     03 CA-RETURN-CODE           PIC 9(2) DISPLAY.
      *     03 CA-RESPONSE-MESSAGE      PIC X(79).
      *    Fields used in Place Order
           03 CA-ORDER-REQUEST.
               05 CA-USERID                PIC X(8).
               05 CA-CHARGE-DEPT           PIC X(8).
               05 CA-ITEM-REF-NUMBER       PIC 9(4) DISPLAY.
               05 CA-QUANTITY-REQ          PIC 9(3) DISPLAY.
               05 FILLER                   PIC X(888).
