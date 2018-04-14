package Sodor

import spinal.core._
import spinal.sim._
import spinal.core.sim._

import scala.util.Random

// sodor's testbench
object SodorSim {
  def main(args: Array[String]) {
    val compiled = SimConfig.withWave.compile{
      val dut = new Sodor ;
      dut.programCounter.io.pc4.simPublic()
      dut.programCounter.io.pc.simPublic()
      dut.programCounter.io.pcNext.simPublic()
      dut.pc.simPublic()
      dut.jalr.simPublic()
      dut.branch.simPublic()
      dut.jump.simPublic()
      dut.iTypeImmediate.simPublic()
      dut.sTypeImmediate.simPublic()
      dut.uTypeImmediate.simPublic()
      dut.rs1.simPublic()
      dut.rs2.simPublic()
      dut.op1Sel.simPublic()
      dut.op2Sel.simPublic()
      dut.wbSel.simPublic()
      dut.aluFun.simPublic()
      dut.aluResult.simPublic()
      dut.rfWen.simPublic()
      dut.regFile.wd.simPublic()
      dut.regFile.wa.simPublic()
      dut.rfWen.simPublic()
      dut.io.dataMemory.addr.simPublic()
      dut
    }

    compiled.doSim("test_ProgramCounter") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      var idx = 0
      while(idx < 10){

        // Drive the dut inputs
        dut.io.instructionMemory.data #= Integer.parseInt("00000000000000000000000000010011", 2)  // NOP (ADDI)

        // Wait a rising edge on the clock
        dut.clockDomain.waitRisingEdge()

        // Check that the dut values match with the reference model ones
        println("pc = ", dut.programCounter.io.pc.toInt)
        println("pc4 = ", dut.programCounter.io.pc4.toInt)
        println("iTypeImmediate = ", dut.iTypeImmediate.toInt)
        //    assert(dut.io.pc.toInt == modelPcNext)
        //   assert(dut.io.pc4.toInt == modelPcNext + 4)

        idx += 1
      }
    }

    compiled.doSim("test_UType") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000000000000000000111111111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("uType = ", dut.uTypeImmediate.toInt)
      assert(dut.uTypeImmediate.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111111111111111111000000000000", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("uType = ", dut.uTypeImmediate.toInt)
      assert(dut.uTypeImmediate.toInt == -4096)
    }

    compiled.doSim("test_ITypeSignExtend") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000000000011111111111111111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("iType = ", dut.iTypeImmediate.toInt)
      assert(dut.iTypeImmediate.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111111111100000000000000000000", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("iType = ", dut.iTypeImmediate.toInt)
      assert(dut.iTypeImmediate.toInt == -1)
    }

    compiled.doSim("test_STypeSignExtend") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000001111111111111000001111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("iType = ", dut.sTypeImmediate.toInt)
      assert(dut.sTypeImmediate.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111110000000000000111110000000", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("sType = ", dut.sTypeImmediate.toInt)
      assert(dut.sTypeImmediate.toInt == -1)
    }

    compiled.doSim("test_JumpTargetGen")
    { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000000000000000000111111111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("pc = ", dut.programCounter.io.pc.toInt)
      println("jump = ", dut.jump.toInt)
      assert(dut.pc.toInt == 0)
      assert(dut.jump.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111111111111111111000000000000", 2)

      // Advance PC to 32 by waiting 8 clocks.
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("pc = ", dut.programCounter.io.pc.toInt)
      println("jump = ", dut.jump.toInt)
      assert(dut.pc.toInt == 32)
      assert(dut.jump.toInt == 30)

      // TODO MORE JAL target tests required..
    }

    compiled.doSim("test_BranchTargetGen")
    { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000001111111111111000001111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("pc = ", dut.programCounter.io.pc.toInt)
      println("branch = ", dut.branch.toInt)
      assert(dut.pc.toInt == 0)
      assert(dut.branch.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111110000000000000111110000000", 2)

      // Advance PC to 32 by waiting 8 clocks.
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("pc = ", dut.programCounter.io.pc.toInt)
      println("branch = ", dut.branch.toInt)
      assert(dut.pc.toInt == 32)
      assert(dut.branch.toInt == 30)

      // TODO MORE BRANCH target tests required..
    }

    compiled.doSim("test_JumpRegTargetGen") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000000000011111111111111111111", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("rs1 = ", dut.rs1.toInt)
      println("jalr = ", dut.jalr.toInt)
      assert(dut.rs1.toInt == 0)
      assert(dut.jalr.toInt == 0)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("11111111111100000000000000000000", 2)

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("rs1 = ", dut.rs1.toInt)
      println("jalr = ", dut.jalr.toInt)
      assert(dut.jalr.toInt == -2)

      // TODO MORE BRANCH target tests required..
    }

    compiled.doSim("test_LW") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("00000000100000000010000110000011", 2) // lw r3, r0, 8
      dut.io.dataMemory.rdata #= 42

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones
      println("rs1 = ", dut.rs1.toInt)
      println("opSel1 = ", dut.op1Sel.toInt)
      println("opSel2 = ", dut.op2Sel.toInt)
      println("aluFun = ", dut.aluFun.toInt)
      println("aluResult = ", dut.aluResult.toInt)
      println("dataMemory.addr = ", dut.io.dataMemory.addr.toInt)
      println("wbSel = ", dut.wbSel.toInt)
      println("wa = ", dut.regFile.wa.toInt)
      println("wd = ", dut.regFile.wd.toInt)
      println("rfWen = ", dut.rfWen.toInt)
      println("rw = ", dut.io.dataMemory.rw.toInt)
      println("val = ", dut.io.dataMemory.valid.toInt)

      assert(dut.rs1.toInt == 0)
      assert(dut.op1Sel.toInt  == 0)
      assert(dut.op2Sel.toInt  == 0)
      assert(dut.aluFun.toInt == 0)
      assert(dut.aluResult.toInt == 8)
      assert(dut.io.dataMemory.addr.toInt == 8)
      assert(dut.wbSel.toInt == 0)
      assert(dut.regFile.wa.toInt == 3)
      assert(dut.regFile.wd.toInt == 42)
      assert(dut.rfWen.toInt == 1)
      assert(dut.io.dataMemory.rw.toInt == 0)
      assert(dut.io.dataMemory.valid.toInt == 1)

      // TODO MORE BRANCH load tests required.
    }

    compiled.doSim("test_SW") { dut =>

      // Fork a process to generate the reset and the clock on the dut
      dut.clockDomain.forkStimulus(period = 10)

      // Drive the dut inputs
      dut.io.instructionMemory.data #= Integer.parseUnsignedInt("0000000000100000000000010001000100011", 2) // sw

      // Wait a rising edge on the clock
      dut.clockDomain.waitRisingEdge()

      // Check that the dut values match with the reference model ones

      println("opSel1 = ", dut.op1Sel.toInt)
      println("opSel2 = ", dut.op2Sel.toInt)
      println("aluFun = ", dut.aluFun.toInt)
      println("rfWen = ", dut.rfWen.toInt)
      println("memRw = ", dut.io.dataMemory.rw.toInt)
      println("memVal = ", dut.io.dataMemory.valid.toInt)
      println("addr = ", dut.io.dataMemory.addr.toInt)
      println("aluResult = ", dut.aluResult.toInt)

      assert(dut.op1Sel.toInt  == 0)
      assert(dut.op2Sel.toInt  == 1)
      assert(dut.aluFun.toInt == 0)
      assert(dut.rfWen.toInt == 0)
      assert(dut.io.dataMemory.rw.toInt == 1)
      assert(dut.io.dataMemory.valid.toInt == 1)
      assert(dut.io.dataMemory.addr.toInt == 68)
      assert(dut.aluResult.toInt == 68)

      println("OK?")

      println("wdata = ", dut.io.dataMemory.wdata.toInt)
      println("rs1 = ", dut.rs1.toInt)
      println("rs2 = ", dut.rs1.toInt)

      assert(dut.rs2.toInt == 0)
      assert(dut.io.dataMemory.wdata.toInt == 0)  // FIXME Should test with non-zero write data.

      // TODO More SW tests required.
    }
  }
}

