open class C1

interface I1

external open class EC1

external class EC2 : C1

external class EC3 : I1, C1

external interface EI1 : I1

interface <!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>I2<!> : EI1

class <!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>C3<!> : EI1

class <!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE, NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>C4<!> : EI1, EC1()

<!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>object O1<!> : EC1()

val x1: Any = <!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>object<!> : EI1 {}
val x2: Any = <!NON_EXTERNAL_TYPE_EXTENDS_EXTERNAL_TYPE!>object<!> : EC1() {}
