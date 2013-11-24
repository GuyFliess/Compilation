REM Run from bin

java -cp bin/;gearley.jar Main pa-2-input/01_shortest.ic > myoutput/01_shortest.ast
java -cp bin/;gearley.jar Main pa-2-input/02_class_structure.ic > myoutput/02_class_structure.ast
java -cp bin/;gearley.jar Main pa-2-input/03_method_structure.ic > myoutput/03_method_structure.ast
java -cp bin/;gearley.jar Main pa-2-input/04_main.ic > myoutput/04_main.ast
java -cp bin/;gearley.jar Main pa-2-input/05_inheritance.ic > myoutput/05_inheritance.ast
java -cp bin/;gearley.jar Main pa-2-input/06_arithmetic.ic > myoutput/06_arithmetic.ast
java -cp bin/;gearley.jar Main pa-2-input/07_dangling_else.ic > myoutput/07_dangling_else.ast
java -cp bin/;gearley.jar Main pa-2-input/11_assign.ic > myoutput/11_assign.ast
java -cp bin/;gearley.jar Main pa-2-input/12_fields_initialized.ic > myoutput/12_fields_initialized.ast
java -cp bin/;gearley.jar Main pa-2-input/13_location.ic > myoutput/13_location.ast
java -cp bin/;gearley.jar Main pa-2-input/14_no_class.ic > myoutput/14_no_class.ast
java -cp bin/;gearley.jar Main pa-2-input/15_simple1.ic > myoutput/15_simple1.ast
java -cp bin/;gearley.jar Main pa-2-input/16_this.ic > myoutput/16_this.ast
java -cp bin/;gearley.jar Main pa-2-input/17_create.ic > myoutput/17_create.ast
java -cp bin/;gearley.jar Main pa-2-input/18_invalid_body.ic > myoutput/18_invalid_body.ast
java -cp bin/;gearley.jar Main pa-2-input/19_method_call.ic > myoutput/19_method_call.ast
java -cp bin/;gearley.jar Main pa-2-input/20_non_class_decl.ic > myoutput/20_non_class_decl.ast
java -cp bin/;gearley.jar Main pa-2-input/21_parenthesis.ic > myoutput/21_parenthesis.ast
java -cp bin/;gearley.jar Main pa-2-input/22_class_in_class.ic > myoutput/22_class_in_class.ast
java -cp bin/;gearley.jar Main pa-2-input/23_create.ic > myoutput/23_create.ast
java -cp bin/;gearley.jar Main pa-2-input/24_declare_many.ic > myoutput/24_declare_many.ast
java -cp bin/;gearley.jar Main pa-2-input/25_if.ic > myoutput/25_if.ast

java -cp bin/;gearley.jar Main pa-2-input/26_inheritance.ic > myoutput/26_inheritance.ast

java -cp bin/;gearley.jar Main pa-2-input/27_length.ic > myoutput/27_length.ast

java -cp bin/;gearley.jar Main pa-2-input/28_while.ic > myoutput/28_while.ast

java -cp bin/;gearley.jar Main pa-2-input/29_access.ic > myoutput/29_access.ast

java -cp bin/;gearley.jar Main pa-2-input/30_type.ic > myoutput/30_type.ast

java -cp bin/;gearley.jar Main pa-2-input/31_blocks.ic > myoutput/31_blocks.ast

java -cp bin/;gearley.jar Main pa-2-input/32_keywords.ic > myoutput/32_keywords.ast

java -cp bin/;gearley.jar Main pa-2-input/33_int-.ic > myoutput/33_int-.ast

java -cp bin/;gearley.jar Main pa-2-input/34_truncated.ic > myoutput/34_truncated.ast

java -cp bin/;gearley.jar Main pa-2-input/Quicksort.ic -Lpa-2-input/libic.sig > myoutput/libic.sig

java -cp bin/;gearley.jar Main pa-2-input/Quicksort.ic > myoutput/Quicksort.ast
                               
fc pa-2-output\01_shortest.ast myoutput\01_shortest.ast
fc pa-2-output\02_class_structure.ast myoutput\02_class_structure.ast
fc pa-2-output\03_method_structure.ast myoutput\03_method_structure.ast
fc pa-2-output\04_main.ast myoutput\04_main.ast
fc pa-2-output\05_inheritance.ast myoutput\05_inheritance.ast
fc pa-2-output\06_arithmetic.ast myoutput\06_arithmetic.ast
fc pa-2-output\07_dangling_else.ast myoutput\07_dangling_else.ast
fc pa-2-output\11_assign.ast myoutput\11_assign.ast
fc pa-2-output\12_fields_initialized.ast myoutput\12_fields_initialized.ast
fc pa-2-output\13_location.ast myoutput\13_location.ast
fc pa-2-output\14_no_class.ast myoutput\14_no_class.ast
fc pa-2-output\15_simple1.ast myoutput\15_simple1.ast
fc pa-2-output\16_this.ast myoutput\16_this.ast
fc pa-2-output\17_create.ast myoutput\17_create.ast
fc pa-2-output\18_invalid_body.ast myoutput\18_invalid_body.ast
fc pa-2-output\19_method_call.ast myoutput\19_method_call.ast
fc pa-2-output\20_non_class_decl.ast myoutput\20_non_class_decl.ast
fc pa-2-output\21_parenthesis.ast myoutput\21_parenthesis.ast
fc pa-2-output\22_class_in_class.ast myoutput\22_class_in_class.ast
fc pa-2-output\23_create.ast myoutput\23_create.ast
fc pa-2-output\24_declare_many.ast myoutput\24_declare_many.ast
fc pa-2-output\25_if.ast myoutput\25_if.ast
fc pa-2-output\26_inheritance.ast myoutput\26_inheritance.ast
fc pa-2-output\27_length.ast myoutput\27_length.ast 
fc pa-2-output\28_while.ast myoutput\28_while.ast
fc pa-2-output\29_access.ast myoutput\29_access.ast
fc pa-2-output\30_type.ast myoutput\30_type.ast
fc pa-2-output\31_blocks.ast myoutput\31_blocks.ast
fc pa-2-output\32_keywords.ast myoutput\32_keywords.ast
fc pa-2-output\33_int-.ast myoutput\33_int-.ast
fc pa-2-output\34_truncated.ast myoutput\34_truncated.ast
fc pa-2-output\Quicksort.ast myoutput\Quicksort.ast