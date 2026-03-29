package io.github.olaflaitinen.schneileopard.graph

import io.github.olaflaitinen.schneileopard.core.*
import scala.collection.mutable

/**
 * An undirected graph representing molecular interactions or pathway membership.
 *
 * The graph stores nodes (genes, proteins, etc.) and edges (interactions, membership). Each edge
 * has an optional weight and type annotation.
 *
 * @param nodes
 *   the set of node identifiers
 * @param edges
 *   the set of edges
 */
case class InteractionGraph(
    nodes: Set[GeneId],
    edges: Set[InteractionEdge]
):
  /**
   * Number of nodes in the graph.
   *
   * @return
   *   the node count
   */
  def nodeCount: Int = nodes.size

  /**
   * Number of edges in the graph.
   *
   * @return
   *   the edge count
   */
  def edgeCount: Int = edges.size

  /**
   * Get all neighbors of a given node.
   *
   * @param node
   *   the node to query
   * @return
   *   the set of neighboring nodes
   */
  def neighbors(node: GeneId): Set[GeneId] =
    edges
      .filter(e => e.from == node || e.to == node)
      .flatMap { e =>
        if e.from == node then Some(e.to) else if e.to == node then Some(e.from) else None
      }

  /**
   * Get all edges incident to a node.
   *
   * @param node
   *   the node to query
   * @return
   *   the set of incident edges
   */
  def incidentEdges(node: GeneId): Set[InteractionEdge] =
    edges.filter(e => e.from == node || e.to == node)

  /**
   * Check if an edge exists between two nodes.
   *
   * @param from
   *   the source node
   * @param to
   *   the target node
   * @return
   *   true if an edge exists
   */
  def hasEdge(from: GeneId, to: GeneId): Boolean =
    edges.exists(e => (e.from == from && e.to == to) || (e.from == to && e.to == from))

  /**
   * Get connected components using a depth-first search.
   *
   * @return
   *   list of connected components, each as a set of nodes
   */
  def connectedComponents: List[Set[GeneId]] =
    val visited    = mutable.Set[GeneId]()
    var components = List[Set[GeneId]]()

    for node <- nodes if !visited.contains(node) do
      val component = dfs(node, visited)
      components = component :: components

    components.reverse

  /**
   * Depth-first search from a starting node.
   *
   * @param start
   *   the starting node
   * @param visited
   *   mutable set to track visited nodes
   * @return
   *   the component reachable from start
   */
  private def dfs(start: GeneId, visited: mutable.Set[GeneId]): Set[GeneId] =
    val stack     = mutable.Stack[GeneId](start)
    var component = Set[GeneId]()

    while stack.nonEmpty do
      val node = stack.pop()
      if !visited.contains(node) then
        visited.add(node)
        component = component + node
        neighbors(node).foreach { n =>
          if !visited.contains(n) then stack.push(n)
        }

    component

/**
 * An edge in an interaction graph.
 *
 * @param from
 *   the source node
 * @param to
 *   the target node
 * @param weight
 *   optional edge weight
 * @param edgeType
 *   optional edge type annotation
 */
case class InteractionEdge(
    from: GeneId,
    to: GeneId,
    weight: Option[Double] = None,
    edgeType: Option[String] = None
)

/**
 * Factory methods for InteractionGraph.
 */
object InteractionGraph:
  /**
   * Create an interaction graph from edges, inferring nodes.
   *
   * @param edges
   *   the edges
   * @return
   *   an InteractionGraph with nodes inferred from edges
   */
  def fromEdges(edges: Set[InteractionEdge]): InteractionGraph =
    val nodes = edges.flatMap(e => Set(e.from, e.to))
    InteractionGraph(nodes, edges)
