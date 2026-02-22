package com.repovista.core.network.mapper

/**
 * Shared DTO -> domain mapping contract.
 *
 * Keep mapper implementations in the feature data layer (or a dedicated shared data module),
 * while network module owns DTO shapes and API contracts.
 */
fun interface DtoMapper<in D, out M> {
    fun map(input: D): M
}

fun <D, M> Iterable<D>.mapWith(mapper: DtoMapper<D, M>): List<M> = map(mapper::map)
