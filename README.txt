
Tools for generating gEDA symbols and schematics in a spreadsheet table
driven project.

    Available via 

       java -jar geda-libra.jar

	  sym

	      Generate symbols from tables.  See sym.txt

	  sch

	      In development, see sch.txt

	  svg

	      Manipulate SVG Path "d" data for use in GSCHEM.  See svg.txt

	  table

	      In development, graphical user interface

TODO

    sym

        Employ path for symbol shape.

    general

        Currently the defined work flow is always from tables because
        classes like Pin and Symbol have been implemented with
        independent producer and consumer modes or methods of
        operation.  This situation is far from ideal for a tool set.
        The repair involves merging attribute subclass children of
        Symbol (Pin) and Schematic (Component) into the Attribute
        "children" list for uniformity under producer and consumer
        modes of operation.

    sch

        Read an existing file and preserve "extra" information.

    table

        Table management

        Generation processes

