package com.hm.core.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * 基礎 UseCase 介面
 */
interface UseCase<in P, out R> {
    suspend operator fun invoke(parameters: P): R
}

/**
 * 基礎 Flow UseCase 介面
 */
interface FlowUseCase<in P, out R> {
    operator fun invoke(parameters: P): Flow<R>
}

/**
 * 無參數的 UseCase
 */
interface NoParamsUseCase<out R> {
    suspend operator fun invoke(): R
}

/**
 * 無參數的 Flow UseCase
 */
interface NoParamsFlowUseCase<out R> {
    operator fun invoke(): Flow<R>
}



