# Week 8 Lab Reflection

## Where does Command decouple UI from business logic?

The Command pattern decouples the UI from business logic by making the PosRemote completely unaware of what it's actually doing. When I press a button, the remote just calls execute on whatever command is stored in that slot. It has no idea if it's adding an item, processing a payment, or anything else. The remote only knows about the Command interface, not the actual OrderService, ProductFactory, or Order classes.

## Why is adapting the legacy printer better than changing domain or vendor class?

Using an adapter is way better than modifying either the domain code or the vendor's printer class. If we changed the CheckoutService to work directly with the thermal printer, my business logic would get mixed with hardware details like byte arrays and printer protocols, making it harder to test and impossible to swap printers later. And I definitely can't modify the vendor's class because I don't own that code—any updates from them would wipe out my changes, and it would break other projects using the same library.