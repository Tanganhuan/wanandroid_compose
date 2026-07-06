package com.go.common.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.ViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

private const val TAG = "SharedViewModelDecoratorTAG"

@Composable
fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(
    viewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
): SharedViewModelStoreNavEntryDecorator<T> {
    val viewModelStoreProvider = rememberViewModelStoreProvider(viewModelStoreOwner)
    return remember(viewModelStoreOwner) {
        SharedViewModelStoreNavEntryDecorator(
            viewModelStoreProvider,
        )
    }
}

class SharedViewModelStoreNavEntryDecorator<T : Any>(
    viewModelStoreProvider: ViewModelStoreProvider
) : NavEntryDecorator<T>(
    onPop = { key ->
        viewModelStoreProvider.clearKey(key)
    },
    decorate = { entry ->
        val localContentKey = entry.contentKey
        val localOwner =
            rememberViewModelStoreOwner(
                viewModelStoreProvider,
                localContentKey,
                savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
            )

        val localValues: MutableList<ProvidedValue<*>> = mutableListOf(LocalViewModelStoreOwner provides localOwner)

        val parentContentKey = entry.metadata[ParentKey]
        if (parentContentKey != null) {
            val parentOwner = rememberViewModelStoreOwner(
                    viewModelStoreProvider,
                    parentContentKey,
                    savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
                )

            localValues.add(LocalSharedViewModelStoreOwner provides parentOwner)
        }

        CompositionLocalProvider(
            values = localValues.toTypedArray()
        ) {
            entry.Content()
        }
    },
) {
    companion object {

        fun parent(key: Any) = metadata {
            put(ParentKey, key)
        }

        private object ParentKey : NavMetadataKey<Any>
    }
}

val LocalSharedViewModelStoreOwner =
    staticCompositionLocalOf<ViewModelStoreOwner> { error("No LocalSharedViewModelStoreOwner provided!") }

fun NavKey.toContentKey() = this.toString()

inline fun <reified T : NavKey> EntryProviderScope<NavKey>.sharedViewModelChildEntry(
    parentKey: NavKey,
    noinline content: @Composable (T) -> Unit
) {
    entry<T>(
        metadata = SharedViewModelStoreNavEntryDecorator.parent(parentKey.toContentKey())
    ) { key ->
        content(key)
    }
}


inline fun <reified T : NavKey> EntryProviderScope<NavKey>.sharedViewModelParentEntry(
    noinline content: @Composable (T) -> Unit
) {
    entry<T>(
        clazzContentKey = { key ->
            key.toContentKey()
        }
    ) {
        content(it)
    }
}



