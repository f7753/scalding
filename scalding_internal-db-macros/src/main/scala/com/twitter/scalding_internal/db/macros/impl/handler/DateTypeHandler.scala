package com.twitter.scalding_internal.db.macros.impl.handler

import scala.language.experimental.macros

import scala.reflect.macros.Context
import scala.reflect.runtime.universe._
import scala.util.Success

import com.twitter.scalding_internal.db.macros.impl.FieldName

object DateTypeHandler {

  def apply[T](c: Context)(implicit fieldName: FieldName,
    defaultValue: Option[c.Expr[String]],
    annotationInfo: List[(c.universe.Type, Option[Int])],
    nullable: Boolean): scala.util.Try[List[ColumnFormat[c.type]]] = {
    import c.universe._

    val helper = new {
      val ctx: c.type = c
      val cfieldName = fieldName
      val cannotationInfo = annotationInfo
    } with AnnotationHelper

    val extracted = for {
      (nextHelper, dateAnno) <- helper.dateAnnotation
      _ <- nextHelper.validateFinished
    } yield (dateAnno)

    extracted.flatMap { t =>
      t match {
        case WithDate => Success(List(ColumnFormat(c)("DATE", None)))
        case WithoutDate => Success(List(ColumnFormat(c)("DATETIME", None)))
      }
    }
  }
}
