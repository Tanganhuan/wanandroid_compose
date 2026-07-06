package com.go.common.navigation3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.result.ResultEventBus
import androidx.navigation3.runtime.result.ResultEventBusNavEntryDecorator


@Composable
fun <T : Any> rememberResultEventBusNavEntryDecorator(bus: ResultEventBus?=null): ResultEventBusNavEntryDecorator<T> =
    remember {
        ResultEventBusNavEntryDecorator(bus = bus?: ResultEventBus())
    }