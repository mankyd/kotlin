/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.frontend.api.fir.symbols

import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.idea.frontend.api.KtAnalysisSession
import org.jetbrains.kotlin.idea.frontend.api.symbols.*
import org.jetbrains.kotlin.idea.frontend.api.symbols.markers.KtSymbolKind
import org.jetbrains.kotlin.idea.frontend.api.symbols.markers.KtSymbolWithKind
import org.jetbrains.kotlin.psi.KtDeclaration

class KtFirSymbolContainingDeclarationProvider(
    private val analysisSession: KtAnalysisSession
) : KtSymbolContainingDeclarationProvider() {
    override fun getContainingDeclaration(symbol: KtSymbolWithKind): KtSymbolWithKind? {
        if (symbol is KtPackageSymbol) return null
        if (symbol.symbolKind == KtSymbolKind.TOP_LEVEL) return null
        return when (symbol.origin) {
            KtSymbolOrigin.SOURCE -> getContainingDeclarationForKotlinInSourceSymbol(symbol)
            KtSymbolOrigin.LIBRARY -> getContainingDeclarationForLibrarySymbol(symbol)
            KtSymbolOrigin.JAVA -> TODO()
            KtSymbolOrigin.SAM_CONSTRUCTOR -> TODO()
        }
    }

    private fun getContainingDeclarationForKotlinInSourceSymbol(symbol: KtSymbolWithKind): KtSymbolWithKind {
        require(symbol.origin == KtSymbolOrigin.SOURCE)
        val psi = symbol.psi ?: error("PSI should present for declaration built by Kotlin code")
        check(psi is KtDeclaration) { "PSI of kotlin declaration should be KtDeclaration" }
        val containingDeclaration = psi.parentOfType<KtDeclaration>()
            ?: error("Containing declaration should present for non-toplevel declaration")
        return with(analysisSession) {
            val containingSymbol = symbolProvider.getSymbol(containingDeclaration)
            check(containingSymbol is KtSymbolWithKind)
            containingSymbol
        }
    }

    private fun getContainingDeclarationForLibrarySymbol(symbol: KtSymbolWithKind): KtSymbolWithKind {
        require(symbol.origin == KtSymbolOrigin.LIBRARY)
        check(symbol.symbolKind == KtSymbolKind.MEMBER)
        val containingClassId = symbol.containingNonLocalClassIdIfMember ?: error("containingClassId should not be null for member declaration")
        val containingClass = analysisSession.symbolProvider.getClassOrObjectSymbolByClassId(containingClassId)
        return containingClass ?: error("Class with id $containingClassId should exists")
    }
}