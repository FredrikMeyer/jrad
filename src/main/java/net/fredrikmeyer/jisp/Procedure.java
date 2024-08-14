package net.fredrikmeyer.jisp;

public sealed interface Procedure extends LispValue permits BuiltInProcedure, UserProcedure {

}
