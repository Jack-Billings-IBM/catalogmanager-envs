      ******************************************************************
      *                                                                *
      * CONTROL BLOCK NAME = DFH0XCP2                                  *
      *                                                                *
      * DESCRIPTIVE NAME = CICS TS  (Samples) Example Application -    *
      *                     Copybook for order dispatcher and stock    *
      *                     manager commarea structures                *
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
      *      defines the datastructure for the order dispatcher and    *
      *      stock manager modules                                     *
      *----------------------------------------------------------------*
      *                                                                *
      * CHANGE ACTIVITY :                                              *
      *      $SEG(DFH0XCP2),COMP(SAMPLES),PROD(CICS TS ):              *
      *                                                                *
      *   PN= REASON REL YYMMDD HDXXIII : REMARKS                      *
      *   $D0= I07544 640 040910 HDIPCB  : EXAMPLE - BASE APPLICATION  *
      *                                                                *
      ******************************************************************
      *    Dispatcher/Stock Manager COMMAREA structure
           03 CA-ORD-REQUEST-ID                PIC X(6).
           03 CA-ORD-RETURN-CODE               PIC 9(2).
           03 CA-ORD-RESPONSE-MESSAGE          PIC X(79).
           03 CA-ORD-REQUEST-SPECIFIC          PIC X(23).
      *    Fields used in Dispatcher
           03 CA-DISPATCH-ORDER REDEFINES CA-ORD-REQUEST-SPECIFIC.
               05 CA-ORD-ITEM-REF-NUMBER       PIC 9(4).
               05 CA-ORD-QUANTITY-REQ          PIC 9(3).
               05 CA-ORD-USERID                PIC X(8).
               05 CA-ORD-CHARGE-DEPT           PIC X(8).
      *    Fields used in Stock Manager
           03 CA-STOCK-MANAGER-UPDATE REDEFINES CA-ORD-REQUEST-SPECIFIC.
               05 CA-STK-ITEM-REF-NUMBER       PIC 9(4).
               05 CA-STK-QUANTITY-REQ          PIC 9(3).
               05 FILLER                       PIC X(16).
