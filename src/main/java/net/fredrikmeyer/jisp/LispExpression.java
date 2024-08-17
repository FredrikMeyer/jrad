package net.fredrikmeyer.jisp;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface LispExpression permits LispLiteral, LispSymbol, LispList {
}

