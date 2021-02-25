      *****************************************************************
      *                                                               *
      * CONTROL BLOCK NAME = DFH0XCP6                                 *
      *                                                               *
      * DESCRIPTIVE NAME = CICS TS  (Samples) Example Application -   *
      *                     Copybook for example application          *
      *                                                               *
      *                                                               *
      *                                                               *
      *      Licensed Materials - Property of IBM                     *
      *                                                               *
      *      "Restricted Materials of IBM"                            *
      *                                                               *
      *      5655-Y04                                                 *
      *                                                               *
      *      (C) Copyright IBM Corp. 2004"                            *
      *                                                               *
      *                                                               *
      *                                                               *
      *                                                               *
      * STATUS = 7.2.0                                                *
      *                                                               *
      * FUNCTION =                                                    *
      *      This copy book is part of the example application and    *
      *      defines the commarea interface to the order dispatcher   *
      *      module                                                   *
      *                                                               *
      *  CHANGE ACTIVITY :                                            *
      *       $SEG(DFH0XCP6),COMP(SAMPLES),PROD(CICS TS ):            *
      *                                                               *
      *   PN= REASON REL YYMMDD HDXXIII : REMARKS                     *
      *   $D0= I07544 640 041129 HDIPCB  : ExampleApp: Outbound suppor*
      *                                                               *
      *****************************************************************
      *    Dispatcher/Stock Manager COMMAREA structure
           03 CA-ORD-REQUEST-ID                PIC X(6).
           03 CA-ORD-RETURN-CODE               PIC 9(2).
           03 CA-ORD-RESPONSE-MESSAGE          PIC X(79).
           03 CA-DISPATCH-ORDER.
               05 CA-ORD-ITEM-REF-NUMBER       PIC 9(4).
               05 CA-ORD-QUANTITY-REQ          PIC 9(3).
               05 CA-ORD-USERID                PIC X(8).
               05 CA-ORD-CHARGE-DEPT           PIC X(8).
