package com.github.andyglow.xml.diff

import scala.util.control.TailCalls._

sealed abstract class Eval[+A] extends Serializable { self =>
  def value: A

  def map[B](f: A => B): Eval[B] =
    flatMap(a => Eval.Now(f(a)))

  def flatMap[B](f: A => Eval[B]): Eval[B] =
    this match {
      case c: Eval.Now[A] =>
        new Eval.FlatMap[B] {
          type Start = A
          val start = () => self
          val run = f
        }

      case c: Eval.FlatMap[A] =>
        new Eval.FlatMap[B] {
          type Start = c.Start
          val start: () => Eval[Start] = c.start
          val run: Start => Eval[B] = (s: c.Start) =>
            new Eval.FlatMap[B] {
              type Start = A
              val start = () => c.run(s)
              val run = f
            }
        }
    }
}

object Eval {
  final case class Now[A](value: A) extends Eval[A]

  def now[A](a: A): Eval[A] = Now(a)

  sealed abstract class FlatMap[A] extends Eval[A] { self =>
    type Start
    val start: () => Eval[Start]
    val run: Start => Eval[A]

    def value: A = evaluate(this)
  }

  sealed abstract private class FnStack[A, B]
  final private case class Ident[A, B](ev: A <:< B) extends FnStack[A, B]
  final private case class Many[A, B, C](first: A => Eval[B], rest: FnStack[B, C]) extends FnStack[A, C]

  private def evaluate[A](e: Eval[A]): A = {
    def loop[A1](curr: Eval[A1], fs: FnStack[A1, A]): TailRec[A] =
      curr match {
        case c: FlatMap[A1] =>
          c.start() match {
            case cc: FlatMap[c.Start] =>
              tailcall(loop(cc.start(), Many(cc.run, Many(c.run, fs))))

            case xx: Now[c.Start] =>
              tailcall(loop(c.run(xx.value), fs))
          }

        case x: Now[A1] =>
          fs match {
            case Many(f, fs) => tailcall(loop(f(x.value), fs))
            case Ident(ev)   => done(ev(x.value))
          }
      }

    loop(e, Ident(implicitly[A <:< A])).result
  }
}
